package com.wexa.graph.model;


public record SkillGap(
    String missingSkillId,
    String missingSkillName,
    String recommendedCourseTitle,
    String courseUrl
) {}
