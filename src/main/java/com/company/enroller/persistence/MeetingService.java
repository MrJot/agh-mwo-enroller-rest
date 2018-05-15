package com.company.enroller.persistence;

import java.util.Collection;
import java.util.HashSet;

import org.hibernate.Query;
import org.hibernate.Transaction;
import org.springframework.stereotype.Component;

import com.company.enroller.model.Meeting;
import com.company.enroller.model.Participant;

@Component("meetingService")
public class MeetingService {

	DatabaseConnector connector;

	public MeetingService() {
		connector = DatabaseConnector.getInstance();
	}

	public Collection<Meeting> getAll() {
		String hql = "FROM Meeting";
		Query query = connector.getSession().createQuery(hql);
		return query.list();
	}

	public Meeting findById(long id) {
		return (Meeting) connector.getSession().get(Meeting.class, id);
	}

	public void addMeeting(Meeting meeting) {
		Transaction transaction = connector.getSession().beginTransaction();
		connector.getSession().save(meeting);
		transaction.commit();
	}
	
	public void addParticipantToTheMeeting(Meeting meeting, Participant participant) {
		Transaction transaction = connector.getSession().beginTransaction();
		meeting.addParticipant(participant);
		connector.getSession().save(meeting);
		transaction.commit();
	}
	
	public boolean ifthereIsAMeeting(long meetingId) {
		String hql = "FROM Meeting p WHERE p.id="+meetingId;
		Query query = connector.getSession().createQuery(hql);
		Collection<Meeting> meeting = query.list();
		if(meeting.size()==0) {
			return false;
		}
		return true;
		
	}

	public boolean hasParticipant(Participant participant, long meetingId) {
		String partLogin = participant.getLogin();
		String hql = "SELECT p FROM Meeting p JOIN p.participants c WHERE c.login='"+partLogin+"' AND p.id="+meetingId;
		Query query = connector.getSession().createQuery(hql);
		if(query.list().size()!=0) {
			return true;
		}
		return false;
	}
	
	
	
	public Collection<Participant> usersEnrolledToTheMeeting(long meetingId){
		String hql = "SELECT p FROM Participant p JOIN p.meetings c WHERE c.id="+meetingId;
		Query query = connector.getSession().createQuery(hql);
		Collection<Participant> list = query.list();
		return list;
	}
	
	public void deleteMeeting(Meeting meeting) {
		Transaction transaction = connector.getSession().beginTransaction();
		connector.getSession().delete(meeting);
		transaction.commit();
	}

	public void modifyMeeting(Meeting meeting) {
		Transaction transaction = connector.getSession().beginTransaction();
		connector.getSession().update(meeting);
		transaction.commit();
		
	}


	


}
