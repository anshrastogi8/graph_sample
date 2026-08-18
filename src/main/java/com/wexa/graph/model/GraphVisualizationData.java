package com.wexa.graph.model;


import java.util.List;

public record GraphVisualizationData(List<NodeDto> nodes, List<EdgeDto> edges) {}