package com.example.microsweeper.rest;

import com.example.microsweeper.model.Score;
import com.example.microsweeper.service.ScoreboardService;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@ApplicationScoped
@Path("/api")
public class RestEndpoints {

    @Inject
    private ScoreboardService scoreboardService;

    @Path("/scoreboard")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Score> getScoreboard() {
        return scoreboardService.getScoreboard();
    }

    @Path("/scoreboard")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Transactional
    public void addScore(Score score) throws Exception {
        scoreboardService.addScore(score);
    }

    @Path("/scoreboard")
    @DELETE
    @Transactional
    public void clearAll() throws Exception {
        scoreboardService.clearScores();
    }
}
