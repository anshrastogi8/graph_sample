package com.wexa.graph.model;


public record RoleRecommendation(
    String roleId,
    String roleTitle,
    long matchedSkillsCount,
    long totalRequiredSkills,
    double matchPercentage
) {}