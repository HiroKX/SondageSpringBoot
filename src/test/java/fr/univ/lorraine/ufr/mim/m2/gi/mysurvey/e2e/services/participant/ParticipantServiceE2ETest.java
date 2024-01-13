package fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.e2e.services.participant;

import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.models.Participant;
import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.repositories.ParticipantRepository;
import fr.univ.lorraine.ufr.mim.m2.gi.mysurvey.services.ParticipantService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Transactional
@Rollback
class ParticipantServiceE2ETest {

    @Autowired
    ParticipantService service;

    @Test
    void testParticipantPersistence() {
       Participant participant1 = new Participant(1L, "Keanu", "Reeves");
       Participant participant2 = new Participant();
       participant2.setParticipantId(2L);
       participant2.setPrenom("Tom");
       participant2.setNom("Hanks");

       
       // Tester 1 participant
       service.create(participant1);
       Participant repoParticipant1 = service.getById(participant1.getParticipantId());
       assertNotNull(repoParticipant1);
       assertEquals(participant1, repoParticipant1);

       // Tester plusieurs participants
        service.create(participant2);
        List<Participant> listParticipants = new ArrayList<>();
        listParticipants.add(participant1);
        listParticipants.add(participant2);
        List<Participant> repoParticipantList = service.getAll();
        assertNotNull(repoParticipantList);
        assertEquals(listParticipants.getFirst(), repoParticipantList.getFirst());
        assertEquals(listParticipants.getLast(), repoParticipantList.getLast());
        assertEquals(listParticipants.get(0), repoParticipantList.get(0));
        assertEquals(listParticipants.get(1), repoParticipantList.get(1));
    }
}
