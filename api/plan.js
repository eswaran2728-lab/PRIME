// Vercel Serverless Function — generates a weekly workout plan via Gemini.
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

  const { days, goal, profile } = req.body || {};
  if (!Array.isArray(days) || !days.length) {
    res.status(400).json({ error: "Missing 'days' array (e.g. ['Mon','Wed','Fri'])." });
    return;
  }

  const prompt =
    "You are a certified strength coach. Build a weekly workout plan for a client " +
    "who can train on these days: " + days.join(", ") + ". " +
    "Their goal is: " + (goal || "general fitness") + ". " +
    (profile ? "Client profile: " + JSON.stringify(profile) + ". " : "") +
    "Return ONLY valid JSON, no markdown, no commentary, in exactly this shape:\n" +
    '{"plan": {"Mon": [{"exercise": "Back Squat", "sets": 4, "reps": 8}], "Wed": []}}\n' +
    "Include an entry for every day listed, 4-6 exercises each, sensible sets/reps for the goal, " +
    "and vary muscle groups sensibly across the week (avoid hitting the same group on consecutive days).";

  try {
    const upstream = await fetch(
      `https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=${apiKey}`,
      {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          contents: [{ role: "user", parts: [{ text: prompt }] }],
          generationConfig: { responseMimeType: "application/json" },
        }),
      }
    );

    const data = await upstream.json();
    if (!upstream.ok) {
      res.status(upstream.status).json({ error: data?.error?.message || "Gemini API error" });
      return;
    }

    const text = data?.candidates?.[0]?.content?.parts?.map(p => p.text).join("") || "{}";
    let parsed;
    try {
      parsed = JSON.parse(text);
    } catch {
      res.status(502).json({ error: "Could not parse plan from AI response." });
      return;
    }

    res.status(200).json({ plan: parsed.plan || {} });
  } catch (err) {
    res.status(502).json({ error: "Failed to reach Gemini API: " + err.message });
  }
}
