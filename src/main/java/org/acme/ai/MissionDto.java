package org.acme.ai;

import dev.langchain4j.model.output.structured.Description;

import java.util.List;

public record MissionDto(
        @Description("Missing Briefing") String missionBriefing,
        @Description("Agent codename of the mission") String agentCodeName,
        @Description("Contact Person") String contactPerson,
        @Description("Full Address of the location") String location,
        @Description("Date and Time") String dateTime,
        @Description("Objective of the mission") String objective,
        @Description("List of instructions") List<String> instructions,
        @Description("Email") String email,
        @Description("Phone number") String phoneNumber
        ) {
}
