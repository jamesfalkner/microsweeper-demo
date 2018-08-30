package com.example.microsweeper.model;

import org.json.JSONObject;

import javax.persistence.*;

@Entity
@Table(name = "SCORES", uniqueConstraints = @UniqueConstraint(columnNames = "scoreId"))
public class Score {

    @Id
    @TableGenerator(
            name = "idGen",
            table = "id_seq_table",
            pkColumnValue = "scoreId"
    )
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "idGen")
    @Column(name = "scoreId", nullable = false)
    private long scoreId;
    private String name;
    private String level;
    private int time;
    private boolean success;

    public Score() {

    }

    public Score(String name, String level, int time, boolean success) {
        this.name = name;
        this.level = level;
        this.time = time;
        this.success = success;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public long getScoreId() {
        return scoreId;
    }

    public void setScoreId(long scoreId) {
        this.scoreId = scoreId;
    }

    @Override
    public String toString() {
        return name + "/" + level + "/" + time + "/" + success + "/" + scoreId;
    }

    public String toJSON() {
        JSONObject obj = new JSONObject();
        obj.put("name", name);
        obj.put("level", level);
        obj.put("time", time);
        obj.put("success", success);
        return obj.toString();
    }

    public static Score fromJSON(String json) {
        JSONObject obj = new JSONObject(json);
        Score score = new Score();
        score.name = obj.getString("name");
        score.level = obj.getString("level");
        score.time = obj.getInt("time");
        score.success = obj.getBoolean("success");
        return score;
    }
}
