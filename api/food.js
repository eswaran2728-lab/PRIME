// Vercel Serverless Function — estimates food + calories from a photo via Gemini vision.
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

  const { imageBase64, mimeType } = req.body || {};
  if (!imageBase64 || typeof imageBase64 !== "string") {
    res.status(400).json({ error: "Missing 'imageBase64' string in request body." });
    return;
  }

  const prompt =
    "You are a nutrition estimation assistant. Look at this photo of a meal and estimate " +
    "its nutritional content. Return ONLY valid JSON, no markdown, no commentary, in exactly " +
    'this shape: {"food": "short description", "calories": number, "protein": number, ' +
    '"carbs": number, "fat": number}. Numbers are grams for protein/carbs/fat and kcal for ' +
    "calories, for the whole plate/portion shown. Give your best estimate even if uncertain.";

  try {
    const upstream = await fetch(
      `https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=${apiKey}`,
      {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          contents: [{
            role: "user",
            parts: [
              { text: prompt },
              { inlineData: { mimeType: mimeType || "image/jpeg", data: imageBase64 } },
            ],
          }],
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
      res.status(502).json({ error: "Could not parse nutrition estimate from AI response." });
      return;
    }

    res.status(200).json({
      food: parsed.food || "Unknown meal",
      calories: Number(parsed.calories) || 0,
      protein: Number(parsed.protein) || 0,
      carbs: Number(parsed.carbs) || 0,
      fat: Number(parsed.fat) || 0,
    });
  } catch (err) {
    res.status(502).json({ error: "Failed to reach Gemini API: " + err.message });
  }
}
