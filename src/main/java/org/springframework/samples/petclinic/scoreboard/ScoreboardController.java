package org.springframework.samples.petclinic.scoreboard;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/scoreboards")
public class ScoreboardController {

    private static final String SCOREBOARD_LIST = "scoreboards/scoreboardList";
    private static final String SCOREBOARD_FORM = "scoreboards/createOrUpdateScoreboardForm";
    
    private ScoreboardService scoreboardService;

    @Autowired
    public ScoreboardController(ScoreboardService scoreboardService) {
        this.scoreboardService = scoreboardService;
    }

    @Transactional
    @GetMapping("/list")
    public ModelAndView showScoreboardList() {
        ModelAndView mav = new ModelAndView(SCOREBOARD_LIST);
        mav.addObject("scoreboards", scoreboardService.findAll());
        return mav;
    }

    @Transactional
    @GetMapping("/new")
    public ModelAndView createScoreboard() {
        ModelAndView mav = new ModelAndView(SCOREBOARD_FORM);
        Scoreboard scoreboard = new Scoreboard();
        mav.addObject(scoreboard);
        return mav;
    }

    @Transactional
    @PostMapping("/new")
    public ModelAndView saveNewScoreboard (@Valid Scoreboard scoreboard, BindingResult br) {
        if (br.hasErrors()) {
            return new ModelAndView(SCOREBOARD_FORM, br.getModel());
        } else {
            scoreboardService.save(scoreboard);
            ModelAndView mav = showScoreboardList();
            mav.addObject("message", "Scoreboard saved successfully");
            return mav;
        }
    }

    @Transactional
    @GetMapping("/{id}/delete")
    public ModelAndView deleteScoreboard(@PathVariable Integer id) {
        scoreboardService.deleteById(id);
        ModelAndView mav = showScoreboardList();
        mav.addObject("message", "Scoreboard deleted successfully");
        return mav;
    }

    @Transactional(readOnly = true)
    @GetMapping("/{id}//edit")
    public ModelAndView editScoreboard(@PathVariable int id){
    	Scoreboard scoreboard = scoreboardService.findById(id);        
        ModelAndView result = new ModelAndView(SCOREBOARD_FORM);
        result.addObject("scoreboard", scoreboard);
        return result;
    }
    
    @Transactional
    @PostMapping("/{id}/edit")
    public ModelAndView saveTile(@PathVariable int id, @Valid Scoreboard scoreboard, BindingResult br){
    	if (br.hasErrors()) {
    		return new ModelAndView(SCOREBOARD_FORM, br.getModel());
    	}
    	Scoreboard scoreboardToBeUpdated= scoreboardService.findById(id);
        BeanUtils.copyProperties(scoreboard,scoreboardToBeUpdated,"id");
        scoreboardService.save(scoreboardToBeUpdated);
        return showScoreboardList();
    }
}