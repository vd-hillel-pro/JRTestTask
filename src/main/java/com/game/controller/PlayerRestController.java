package com.game.controller;

import com.game.entity.Player;
import com.game.service.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/rest")
public class PlayerRestController {

    //final Logger logger = LoggerFactory.getLogger(PlayerRestController.class);

    private PlayerService playerService;

    @Autowired
    public void setPlayerService(PlayerService playerService) {
        this.playerService = playerService;
    }

    @GetMapping(path = "/players", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Player>> getAll(@RequestParam Map<String, String> params
            /*
            @RequestParam(name = "name",required = false) String name,
            @RequestParam(name = "title",required = false) String title,
            @RequestParam(name = "race",required = false) Race race,
            @RequestParam(name = "profession",required = false) Profession profession,
            @RequestParam(name = "after",required = false, defaultValue = "0") Long after,
            @RequestParam(name = "before",required = false, defaultValue = "9223372036854775807") Long before,
            @RequestParam(name = "banned",required = false) Boolean banned,
            @RequestParam(name = "minExperience",required = false, defaultValue = "0") Integer minExperience,
            @RequestParam(name = "maxExperience",required = false, defaultValue = "10000000") Integer maxExperience,
            @RequestParam(name = "minLevel",required = false, defaultValue = "0") Integer minLevel,
            @RequestParam(name = "maxLevel",required = false, defaultValue = "214748364") Integer maxLevel,
            @RequestParam(name = "order",required = false, defaultValue = "ID") PlayerOrder order,
            @RequestParam(name = "pageNumber",required = false, defaultValue = "0") Integer pageNumber,
            @RequestParam(name = "pageSize",required = false, defaultValue = "3") Integer pageSize
            */
    ) {
        Page<Player> players = playerService.getAll(params);
        System.out.println(players.getContent());
        return new ResponseEntity<>(players.getContent(), HttpStatus.OK);
    }

    @GetMapping(path = "/players/count")
    public ResponseEntity<Integer> getCount(@RequestParam Map<String, String> params) {
        Page<Player> players = playerService.getAll(params);
        return new ResponseEntity<>(Math.toIntExact(players.getTotalElements()), HttpStatus.OK);
    }

    @PostMapping(path = "/players")
    public ResponseEntity<Player> createPlayer(@RequestBody Player player) {
        if (
                player.getName() == null ||
                        player.getTitle() == null ||
                        player.getRace() == null ||
                        player.getProfession() == null ||
                        player.getBirthday() == null ||
                        player.getExperience() == null
        ) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        if (!playerService.isParamsValid(player)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(playerService.createPlayer(player), HttpStatus.OK);
    }

    @GetMapping(value = "/players/{id}")
    public ResponseEntity<Player> get(@PathVariable("id") Long id) {
        if (this.playerService.isIdValid(id)) {
            Optional<Player> optional = this.playerService.get(id);
            return optional.map(player -> new ResponseEntity<>(player, HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
        } else return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @PostMapping(value = "/players/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Player> updatePlayer(@PathVariable(name = "id") Long id, @RequestBody Player player) {
        if (id == null) {
            return new ResponseEntity<>(player, HttpStatus.OK);
        }
        if (!this.playerService.isIdValid(id)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        if (player.getName() == null &&
                player.getTitle() == null &&
                player.getRace() == null &&
                player.getProfession() == null &&
                player.getBirthday() == null &&
                player.getExperience() == null) {
            return new ResponseEntity<>(this.playerService.getP(id), HttpStatus.OK);
        }
        if (!playerService.isParamsValid(player)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        if (!this.playerService.exists(id)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        Player playerNew = this.playerService.updatePlayer(id, player);
        return new ResponseEntity<>(playerNew, HttpStatus.OK);
    }

    @DeleteMapping(value = "/players/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Player> delete(@PathVariable("id") Long id) {
        if (this.playerService.isIdValid(id)) {
            if (this.playerService.exists(id)) {
                this.playerService.delete(id);
                return new ResponseEntity<>(HttpStatus.OK);
            }
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
}
