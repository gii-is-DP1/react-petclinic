package org.springframework.samples.petclinic.scoreboard;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.samples.petclinic.game.Game;
import org.springframework.samples.petclinic.user.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ScoreboardService {

    ScoreboardRepository scoreboardRepository;

    @Autowired
    protected UserService userService;

    @Autowired
    ScoreboardService(ScoreboardRepository scoreboardRepository) {
        this.scoreboardRepository = scoreboardRepository;
    }

    List<Scoreboard> findAll() {
        return scoreboardRepository.findAll();
    }
    
    public List<Scoreboard> findByUsername(String username) {
        return scoreboardRepository.findByUsername(username);
    }

    public List<Scoreboard> findByGameId(Integer gameId) {
        return scoreboardRepository.findByGameId(gameId);
    }

    public Scoreboard findByGameIdAndUsername(Integer gameId, String username) {
        return scoreboardRepository.findByGameIdAndUsername(gameId, username);
    }

    public Scoreboard findById(Integer id) {
        return scoreboardRepository.findById(id).get();
    }

    public void deleteById(Integer id) {
        scoreboardRepository.deleteById(id);
    }

    public void save(Scoreboard scoreboard) {
        scoreboardRepository.save(scoreboard);
    }

    @Transactional
    public void increaseScore(Game game, Integer score, String username) {
        Scoreboard scoreboard = scoreboardRepository.findByGameIdAndUsername(game.getId(), username);
        scoreboard.setScore(scoreboard.getScore() + score);
        scoreboardRepository.save(scoreboard);    
    }
}
