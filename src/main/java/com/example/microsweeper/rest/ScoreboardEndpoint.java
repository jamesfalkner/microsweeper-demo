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
@Path("/scoreboard")
public class ScoreboardEndpoint {

    @Inject
    private ScoreboardService scoreboardService;
 
    @Inject
    ExternalService externalService;
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Score> getScoreboard() {
        return scoreboardService.getScoreboard();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public void addScore(Score score) throws Exception {
        scoreboardService.addScore(score);
        externalService.callExternal();
    }

    @DELETE
    public void clearAll() throws Exception {
        scoreboardService.clearScores();
    }
}
