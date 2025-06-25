package org.acme;


import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Lob;

import java.time.LocalDateTime;
import java.util.List;

@Entity
public class Mission extends PanacheEntity {

    public String missionBriefing;
    public String agentCodeName;
    public String contactPerson;
    public String location;
    public LocalDateTime dateTime;
    @Lob public String objective;
    @ElementCollection public List<String> instructions;
    public String email;
    public String phone;
    @Lob public String fullReport;

}
