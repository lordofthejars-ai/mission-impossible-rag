package org.acme.ai;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import io.quarkiverse.langchain4j.RegisterAiService;

@RegisterAiService
public interface MissionExtractor {

    @SystemMessage("""
            You need to extract details from  mission briefings.
            
            The fields you can extract are:
            
            - Mission Briefing
            - Agent Code Name
            - Contact Person
            - Location
            - Date and Time (transform if necessary the format into ISO LOCAL DATE TIME)
            - Objective
            - Communication Email
            - Phone Number
            """)
    MissionDto extract(@UserMessage String message);
}
