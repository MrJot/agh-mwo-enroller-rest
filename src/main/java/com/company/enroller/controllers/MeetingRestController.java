package com.company.enroller.controllers;

import java.util.Collection;
import java.util.HashSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.company.enroller.model.Meeting;
import com.company.enroller.model.Participant;
import com.company.enroller.persistence.MeetingService;
import com.company.enroller.persistence.ParticipantService;

/*
Wersja BASIC ---- DONE
Pobieranie listy wszystkich spotkań ----DONE
Pobieranie listy pojedyncznego spotkania ----DONE
Dodawanie spotkań ---DONE
Dodawanie uczestnika do spotkania ---DONE
Pobieranie uczestników spotkania ---DONE

Wersja GOLD (dodatkowo do BASIC)

Usuwanie spotkań ---DONE
Aktualizację spotkań ---DONE
Usuwanie uczestnika ze spotkania
 */

@RestController
@RequestMapping("/meetings")
public class MeetingRestController {

	@Autowired
	MeetingService meetingService;
	ParticipantService participantService;

	@RequestMapping(value = "", method = RequestMethod.GET)
	public ResponseEntity<?> getMeetings() {
		Collection<Meeting> meetings = meetingService.getAll();
		return new ResponseEntity<Collection<Meeting>>(meetings, HttpStatus.OK);
	}

	
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public ResponseEntity<?> getMeeting(@PathVariable("id") long id) {
		Meeting meeting = meetingService.findById(id);
		if (meeting == null) {
			return new ResponseEntity(HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<Meeting>(meeting, HttpStatus.OK);
	}


	@RequestMapping(value = "", method = RequestMethod.POST)
	public ResponseEntity<?> addMeeting(@RequestBody Meeting meeting){
		if (meetingService.findById(meeting.getId())!=null) {
			return new ResponseEntity("Unable to create the meeting. A meeting with Id " + 
					meeting.getId() + " already exist.", HttpStatus.CONFLICT);
		}

		meetingService.addMeeting(meeting);

		return new ResponseEntity<Meeting>(meeting, HttpStatus.OK);


	}
	
	
	
	@RequestMapping(value = "/{id}", method = RequestMethod.POST)
	public ResponseEntity<?> addParticipantToTheMeeting(@PathVariable("id") long meetingId,
			@RequestBody Participant participant){
		Meeting meeting = meetingService.findById(meetingId);
			if (meetingService.hasParticipant(participant, meetingId)) {
				return new ResponseEntity("Unable to add Participant. Participant with login: " + 
						participant.getLogin() + " already enrolled to the meeting.", HttpStatus.CONFLICT);
			}
		meetingService.addParticipantToTheMeeting(meeting, participant);
		return new ResponseEntity<Meeting>(meeting, HttpStatus.OK);
	}
	

	@RequestMapping(value = "/{id}/enrolledUsers", method = RequestMethod.GET)
	public ResponseEntity<?> showParticipantEnrolledToTheMeeting(@PathVariable("id") long meetingId){
		Meeting meeting = meetingService.findById(meetingId);
		Collection<Participant> participantList = meetingService.usersEnrolledToTheMeeting(meetingId);
		if(meetingService.ifthereIsAMeeting(meetingId)==false) {
			return new ResponseEntity("Meeting with specified id does not exists", HttpStatus.BAD_REQUEST);
		}
		if (participantList.isEmpty()) {
			return new ResponseEntity("There are no users enrolled to this meeting", HttpStatus.BAD_REQUEST);
		}
		
		Collection<String> userNames = new HashSet<>();
		for (Participant part:participantList) {
			userNames.add(part.getLogin());
		}
		
		return new ResponseEntity<Collection<String>>(userNames, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<?> deleteMeeting(@PathVariable("id") long meetingId){
		Meeting meeting = meetingService.findById(meetingId);
		if (meeting == null) {
			return new ResponseEntity(HttpStatus.NOT_FOUND);
		}
		meetingService.deleteMeeting(meeting);
		return new ResponseEntity<Meeting>(meeting, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/{id}", method = RequestMethod.PUT)
	public ResponseEntity<?> modifyPMeeting(@PathVariable("id") long meetingId, @RequestBody Meeting updatedMeeting){
		Meeting meeting = meetingService.findById(meetingId);
		if (meeting == null) {
			return new ResponseEntity(HttpStatus.NOT_FOUND);
		}
		meeting.setDate(updatedMeeting.getDate());
		meeting.setDescription(updatedMeeting.getDescription());
		meeting.setTitle(updatedMeeting.getTitle());
		meetingService.modifyMeeting(meeting);

		return new ResponseEntity<Meeting>(meeting, HttpStatus.OK);

	}
	
	

	

	
	

}
