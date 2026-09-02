// Vercel Serverless Function — proxies chat requests to Gemini so the
// API key never reaches the browser. Configure GEMINI_API_KEY in the
// Vercel project's Environment Variables.
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
        "and suggest one concrete next action. Keep replies concise (under " +
        "150 words) unless asked for detail.\n\nUser data snapshot:\n" +
        JSON.stringify(context || {}, null, 2),
    }],
  };

  const contents = [
    systemInstruction,
    ...(Array.isArray(history) ? history : []).map(m => ({
      role: m.role === "assistant" ? "model" : "user",
      parts: [{ text: String(m.text || "") }],
    })),
    { role: "user", parts: [{ text: message }] },
  ];

  try {
    const upstream = await fetch(
      `https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=${apiKey}`,
      {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ contents }),
      }
    );

    const data = await upstream.json();

    if (!upstream.ok) {
      res.status(upstream.status).json({ error: data?.error?.message || "Gemini API error" });
      return;
    }

    const reply = data?.candidates?.[0]?.content?.parts?.map(p => p.text).join("") || "";
    res.status(200).json({ reply });
  } catch (err) {
    res.status(502).json({ error: "Failed to reach Gemini API: " + err.message });
  }
}
