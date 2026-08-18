package com.wexa.graph.controller;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.wexa.graph.model.GraphVisualizationData;
import com.wexa.graph.model.RoleRecommendation;
import com.wexa.graph.model.SkillGap;
import com.wexa.graph.service.SkillGraphService;

import java.util.List;

@Controller
public class GraphController {

    private final SkillGraphService graphService;

    public GraphController(SkillGraphService graphService) {
        this.graphService = graphService;
    }

    @GetMapping("/")
    public String index(
        @RequestParam(defaultValue = "dev_alex") String personId,
        @RequestParam(required = false) String targetRoleId,
        Model model
    ) {
        boolean dbOk = graphService.isDatabaseConnected();
        model.addAttribute("dbConnected", dbOk);
        model.addAttribute("selectedPersonId", personId);

        if (dbOk) {
            List<RoleRecommendation> recommendations = graphService.getRecommendedRoles(personId);
            model.addAttribute("recommendations", recommendations);

            if (targetRoleId != null && !targetRoleId.isBlank()) {
                List<SkillGap> skillGaps = graphService.getSkillGapAnalysis(personId, targetRoleId);
                model.addAttribute("selectedRoleId", targetRoleId);
                model.addAttribute("skillGaps", skillGaps);
            }
        }

        return "index";
    }

    @GetMapping("/api/graph-data")
    @ResponseBody
    public GraphVisualizationData getGraphData() {
        if (!graphService.isDatabaseConnected()) {
            return new GraphVisualizationData(List.of(), List.of());
        }
        return graphService.getVisualGraphData();
    }
}
