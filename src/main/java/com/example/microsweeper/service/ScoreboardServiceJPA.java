package com.example.microsweeper.service;

import com.example.microsweeper.model.Score;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Default;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.List;

@ApplicationScoped
@Default
public class ScoreboardServiceJPA implements ScoreboardService {

    @PersistenceContext(unitName = "ScoresPU")
    private EntityManager em;

    @Override
    public List<Score> getScoreboard() {
        return em.createQuery("SELECT s FROM Score s", Score.class).getResultList();
    }

    @Override
    @Transactional
    public void addScore(Score score) {
         em.persist(score);
            em.flush();
    }

    @Override
    public void clearScores() {
        em.createQuery("DELETE FROM Score").executeUpdate();
        em.flush();
    }
}
