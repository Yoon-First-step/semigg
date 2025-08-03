package semigg.semi.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import semigg.semi.dto.SummonerDto;
import semigg.semi.service.RiotApiService;

@RestController
@RequestMapping("/api/riot")
public class RiotController {

    private final RiotApiService riotApiService;

    public RiotController(RiotApiService riotApiService) {
        this.riotApiService = riotApiService;
    }

    @GetMapping("/summoner")
    public SummonerDto getSummoner(@RequestParam("name") String name) {
        return riotApiService.getSummonerByName(name);
    }
}
