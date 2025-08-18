package com.college.PlacementApl.ResumeAnalyais;


import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.college.PlacementApl.dtos.ResumeProfile;



@Component
public class ResumeParser {

    // Very small skills ontology. Expand as needed or load from DB/file.
    private static final Set<String> SKILLS = Set.of(
        "java","spring","hibernate","sql","mysql","postgresql","rest","aws","docker","kubernetes",
        "react","angular","javascript","html","css","python","c++","machine learning", "nlp"
    );

    public ResumeProfile parse(MultipartFile file) throws IOException {
        String text = extractTextFromPdf(file.getInputStream());
        ResumeProfile profile = new ResumeProfile();
        profile.setRawText(text);

        profile.setEmail(findEmail(text));
        profile.setPhone(findPhone(text));
        profile.setName(guessName(text));
        profile.setSkills(findSkills(text));
        profile.setExperiences(findExperienceSnippets(text));
        return profile;
    }

    private String extractTextFromPdf(InputStream is) throws IOException {
        try (PDDocument doc = PDDocument.load(is)) {
            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(doc);
            if (text == null) text = "";
            return text.replaceAll("\\r", "\n");
        }
    }

    private String findEmail(String text) {
        Pattern p = Pattern.compile("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-z]{2,}", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(text);
        return m.find() ? m.group(0) : null;
    }

    private String findPhone(String text) {
        // naive phone matcher for indian + international formats
        Pattern p = Pattern.compile("(?:\\+?\\d{1,3}[\\s-]?)?(?:\\d{10}|\\d{3}[\\s-]\\d{3}[\\s-]\\d{4}|\\(\\d{3}\\)\\s?\\d{3}-\\d{4})");
        Matcher m = p.matcher(text);
        return m.find() ? m.group(0) : null;
    }

    private String guessName(String text) {
        // Simple heuristic: first non-empty line that contains letters and is not "email" or "phone".
        String[] lines = text.split("\\r?\\n");
        for (String l : lines) {
            String s = l.trim();
            if (s.length() < 3 || s.length() > 60) continue;
            if (s.toLowerCase().contains("email") || s.toLowerCase().contains("phone") || s.toLowerCase().contains("resume")) continue;
            // if contains at least one space (first + last)
            if (s.matches(".*[A-Za-z].*") && s.split("\\s+").length <= 4) {
                return s;
            }
        }
        return null;
    }

    private List<String> findSkills(String text) {
        String lower = text.toLowerCase();
        return SKILLS.stream()
                .filter(skill -> lower.contains(skill))
                .sorted()
                .collect(Collectors.toList());
    }

    private List<String> findExperienceSnippets(String text) {
        // naive: split into paragraphs and return those that contain "experience" or years or company keywords
        String[] parts = text.split("\\n\\n+");
        List<String> snippets = new ArrayList<>();
        for (String p : parts) {
            String lower = p.toLowerCase();
            if (lower.contains("experience") || lower.matches(".*\\b\\d{4}\\b.*") || lower.contains("worked")) {
                String s = p.trim();
                if (s.length() > 20 && s.length() < 1000) snippets.add(s);
            }
            if (snippets.size() >= 5) break;
        }
        return snippets;
    }
}

