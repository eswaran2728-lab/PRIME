// Vercel Serverless Function — proxies chat requests to Gemini so the
// API key never reaches the browser. Configure GEMINI_API_KEY in the
// Vercel project's Environment Variables.
//
// The coach can take actions (log a workout set, a meal, water, a habit
// checkbox, a body measurement, a time block) via Gemini function
// calling. This function has no access to the browser's localStorage,
// so it never mutates anything itself — it loops with Gemini until a
// final text reply is produced, collecting every requested action along
// the way, and returns { reply, actions } for the client to apply.

const TOOLS = [{
  functionDeclarations: [
    {
      name: "log_workout_set",
      description: "Log one completed set of an exercise to today's workout.",
      parameters: {
        type: "OBJECT",
        properties: {
          exercise: { type: "STRING", description: "Exercise name, e.g. 'Barbell Bench Press'" },
          weightKg: { type: "NUMBER", description: "Weight used in kg (0 if bodyweight)" },
          reps: { type: "NUMBER", description: "Reps completed" },
        },
        required: ["exercise", "reps"],
      },
    },
    {
      name: "log_meal",
      description: "Log a food/meal entry for today with its nutrition.",
      parameters: {
        type: "OBJECT",
        properties: {
          food: { type: "STRING" },
          calories: { type: "NUMBER" },
          protein: { type: "NUMBER", description: "grams" },
          carbs: { type: "NUMBER", description: "grams" },
          fat: { type: "NUMBER", description: "grams" },
          mealType: { type: "STRING", enum: ["breakfast", "lunch", "dinner", "snack"] },
        },
        required: ["food", "calories"],
      },
    },
    {
      name: "log_water",
      description: "Add water intake in milliliters for today.",
      parameters: {
        type: "OBJECT",
        properties: { amountMl: { type: "NUMBER" } },
        required: ["amountMl"],
      },
    },
    {
      name: "toggle_habit",
      description: "Mark a habit as done (or undone) for today. Creates the habit if it doesn't exist yet.",
      parameters: {
        type: "OBJECT",
        properties: {
          name: { type: "STRING" },
          done: { type: "BOOLEAN", description: "true to mark complete, false to un-mark" },
        },
        required: ["name", "done"],
      },
    },
    {
      name: "log_body_measurement",
      description: "Log a body measurement entry for today.",
      parameters: {
        type: "OBJECT",
        properties: {
          weightKg: { type: "NUMBER" },
          waistCm: { type: "NUMBER" },
        },
        required: ["weightKg"],
      },
    },
    {
      name: "add_time_block",
      description: "Add a time block to today's schedule.",
      parameters: {
        type: "OBJECT",
        properties: {
          title: { type: "STRING" },
          start: { type: "STRING", description: "HH:MM 24h" },
          end: { type: "STRING", description: "HH:MM 24h" },
        },
        required: ["title"],
      },
    },
  ],
}];

export default async function handler(req, res) {
  if (req.method !== "POST") {
    res.status(405).json({ error: "Method not allowed" });
    return;
  }

  const apiKey = process.env.GEMINI_API_KEY;
  if (!apiKey) {
    res.status(500).json({ error: "GEMINI_API_KEY is not configured on the server." });
    return;
  }

  const { message, history, context } = req.body || {};
  if (!message || typeof message !== "string") {
    res.status(400).json({ error: "Missing 'message' string in request body." });
    return;
  }

  const systemInstruction = {
    role: "user",
    parts: [{
      text:
        "You are the personal AI coach inside Remix PRIME, an app for total " +
        "self-mastery across workout, nutrition, habits, time-blocking, and " +
        "body tracking. You are warm, direct, and practical. Use the JSON " +
        "snapshot of the user's recent data below to personalize your advice " +
        "— reference specific numbers when relevant, notice gaps or streaks, " +
        "and suggest one concrete next action.\n\n" +
        "You can take real actions using the provided tools: logging a " +
        "workout set, a meal, water, a habit completion, a body measurement, " +
        "or a time block. When the user describes something they did or want " +
        "logged ('I did 3 sets of squats at 80kg for 8 reps', 'log a banana', " +
        "'I drank 500ml water', 'mark meditation done'), call the matching " +
        "tool(s) instead of just describing it in text. You may call multiple " +
        "tools in one turn. For food, estimate reasonable calories/macros if " +
        "the user doesn't give exact numbers. After taking actions, briefly " +
        "confirm what you logged in your reply. Keep replies concise (under " +
        "150 words) unless asked for detail.\n\nUser data snapshot:\n" +
        JSON.stringify(context || {}, null, 2),
    }],
  };

  let contents = [
    systemInstruction,
    ...(Array.isArray(history) ? history : []).map(m => ({
      role: m.role === "assistant" ? "model" : "user",
      parts: [{ text: String(m.text || "") }],
    })),
    { role: "user", parts: [{ text: message }] },
  ];

  const collectedActions = [];

  try {
    for (let iteration = 0; iteration < 4; iteration++) {
      const upstream = await fetch(
        `https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=${apiKey}`,
        {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify({ contents, tools: TOOLS }),
        }
      );

      const data = await upstream.json();
      if (!upstream.ok) {
        res.status(upstream.status).json({ error: data?.error?.message || "Gemini API error" });
        return;
      }

      const parts = data?.candidates?.[0]?.content?.parts || [];
      const functionCalls = parts.filter(p => p.functionCall).map(p => p.functionCall);

      if (!functionCalls.length) {
        const reply = parts.map(p => p.text || "").join("");
        res.status(200).json({ reply, actions: collectedActions });
        return;
      }

      contents.push({ role: "model", parts: parts });
      contents.push({
        role: "function",
        parts: functionCalls.map(fc => ({
          functionResponse: { name: fc.name, response: { status: "ok" } },
        })),
      });

      for (const fc of functionCalls) {
        collectedActions.push({ name: fc.name, args: fc.args || {} });
      }
    }

    res.status(200).json({ reply: "Done — logged what you asked.", actions: collectedActions });
  } catch (err) {
    res.status(502).json({ error: "Failed to reach Gemini API: " + err.message });
  }
}
