package com.example.microsweeper.service;

import com.example.microsweeper.model.Score;

import javax.annotation.Resource;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Default;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import javax.transaction.UserTransaction;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@ApplicationScoped
@Default
public class ScoreboardServiceJPA implements ScoreboardService {

    @PersistenceContext(unitName = "ScoresPU")
    private EntityManager em;

    @Resource
    private UserTransaction utx;

    @Override
    public List<Score> getScoreboard() {
        return em.createQuery("SELECT s FROM Score s", Score.class).getResultList();
    }

    @Override
    @Transactional
    public void addScore(Score score) throws Exception {
         em.persist(score);
            em.flush();
    }

    @Override
    public void clearScores() {
        em.createQuery("DELETE FROM Score").executeUpdate();
        em.flush();
    }
}
