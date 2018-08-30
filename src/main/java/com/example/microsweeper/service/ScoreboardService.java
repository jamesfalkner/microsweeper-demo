package com.example.microsweeper.service;

import com.example.microsweeper.model.Score;

import java.util.List;

public interface ScoreboardService {

    List<Score> getScoreboard();

    void addScore(Score score) throws Exception;

    void clearScores() throws Exception;


}
