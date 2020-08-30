package com.medic.mediscreen.service;

import com.medic.mediscreen.domain.PatHistory;
import com.medic.mediscreen.domain.PatInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.*;

/**
 * classic CRUD methods in order to managing de Patient table in the database
 */

@Service
@Slf4j
public class PatientService {

    private Set<String> termList;

    public PatientService(@Value("termesDeclencheurs") String terms) {
        termList = new HashSet<>(Arrays.asList(terms.split(",")));
    }

    public String getAssessment(PatInfo patInfo) {
        int occurences = getOccurrences(patInfo.getPatHistoryList());
        int age = getAge(patInfo.getPatient().getDob());
        char sex = patInfo.getPatient().getSex();
        String assess;

        if (occurences <2) {
            assess = "None";
        }
        else if (occurences <3 && age >30){
            assess = "Borderline";
        }
        else if (occurences < 5 && age <=30 && sex=='M'){
            assess = "In Danger";
        }
        else if (occurences < 7 && age <=30 && sex=='S'){
            assess = "In Danger";
        }
        else if (occurences < 8 && age >30){
            assess = "In Danger";
        }
        else {
            assess = "Early onset";
        }

            return "Patient: Test "
                    + patInfo.getPatient().getFamily()
                    + " (age "
                    + age
                    + ") diabetes assessment is: "
                    + assess;
    }

    private int getOccurrences(List<PatHistory> patHistoryList) {
        String allHistory = null;
        for (PatHistory patHistory : patHistoryList) {
            allHistory += patHistory.toString() + " ";
        }
        Map<String, Integer> allWordOccurrences = new HashMap<>();
        List<String> wordList = Arrays.asList(allHistory.split(" "));
        for (String a : wordList) {
            Integer freq = allWordOccurrences.get(a);
            allWordOccurrences.put(a.toLowerCase(), (freq == null) ? 1 : freq + 1);
        }
        int occurences = 0;
        for (String term : termList) {
            occurences += allWordOccurrences.get(term.toLowerCase());
        }
        return occurences;
    }

    private int getAge(Date dob) {
        LocalDate localDob = Instant.ofEpochMilli(dob.getTime())
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
        return Period.between(localDob, LocalDate.now()).getYears();
    }


}