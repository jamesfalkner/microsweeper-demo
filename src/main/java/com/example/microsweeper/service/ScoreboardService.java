package com.example.microsweeper.service;

import com.example.microsweeper.model.Score;
import java.util.List;

public interface ScoreboardService {

    public List<Score> getScoreboard();

    public void addScore(Score score) throws Exception;

    public void clearScores() throws Exception;


}
