package com.example.microsweeper.rest;

import com.example.microsweeper.model.Score;
import com.example.microsweeper.service.ScoreboardService;

import javax.annotation.Resource;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import javax.transaction.UserTransaction;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@ApplicationScoped
@Path("/api")
public class RestEndpoints {

    @PersistenceContext(unitName = "ScoresPU")
    private EntityManager em;

    @Inject
    private ScoreboardService scoreboardService;

    @Resource
    private UserTransaction utx;

    //    @Inject
//    @ConfigProperty(name = "")
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
