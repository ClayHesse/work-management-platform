package com.clay.wmp.team.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateUpdateTeamRequest(@NotBlank @Size(max = 100) String name) {}
