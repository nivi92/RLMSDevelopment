package com.rlms.dao;

import java.util.Date;
import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.rlms.constants.RLMSConstants;
import com.rlms.constants.Status;
import com.rlms.contract.ComplaintsDtlsDto;
import com.rlms.contract.SiteVisitReportDto;
import com.rlms.contract.TechnicianWiseReportDTO;
import com.rlms.model.RlmsComplaintMaster;
import com.rlms.model.RlmsComplaintTechMapDtls;
import com.rlms.model.RlmsSiteVisitDtls;

@Repository
public class ComplaintsDaoImpl implements ComplaintsDao{

	@Autowired
	private SessionFactory sessionFactory;
	
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	@SuppressWarnings("unchecked")
	public List<RlmsComplaintTechMapDtls> getAllComplaintsAssigned(Integer userRoleId, List<Integer> statusList){
		 Session session = this.sessionFactory.getCurrentSession();
		 Criteria criteria = session.createCriteria(RlmsComplaintTechMapDtls.class);
				 criteria.createAlias("complaintMaster", "cbm");
		 	criteria.add(Restrictions.eq("userRoles.userRoleId", userRoleId));
				 criteria.add(Restrictions.in("cbm.status", statusList));
				 criteria.add(Restrictions.eq("activeFlag", RLMSConstants.ACTIVE.getId()));
		 List<RlmsComplaintTechMapDtls> listOfAllcomplaints = criteria.list();
		 return listOfAllcomplaints;
	}
	
	public List<RlmsComplaintMaster> getAllComplaintsForBranchOrCustomer(Integer branchCompanyMapId, Integer branchCustomerMapId, List<Integer> statusList,Integer callType){
		 Session session = this.sessionFactory.getCurrentSession();
		 Criteria criteria = session.createCriteria(RlmsComplaintMaster.class);
		 criteria.createAlias("liftCustomerMap.branchCustomerMap", "bcm");
		 criteria.createAlias("bcm.companyBranchMapDtls", "cbm");
				 if(null != branchCompanyMapId){
					 criteria.add(Restrictions.eq("cbm.companyBranchMapId", branchCompanyMapId));
				 }
				 if(null != branchCustomerMapId){
					 criteria.add(Restrictions.eq("bcm.branchCustomerMapId", branchCustomerMapId));
				 }
				 criteria.add(Restrictions.in("status", statusList));
				 criteria.add(Restrictions.eq("activeFlag", RLMSConstants.ACTIVE.getId()));
				 if(null != callType){
					 criteria.add(Restrictions.eq("callType", callType));
				 }
		 List<RlmsComplaintMaster> listOfAllcomplaints = criteria.list();
		 return listOfAllcomplaints;
	}
	
	public Integer saveComplaintM(RlmsComplaintMaster complaintMaster){
		Integer complaintsId = (Integer) this.sessionFactory.getCurrentSession().save(complaintMaster);
		return complaintsId;
	}
	
	public void mergeComplaintM(RlmsComplaintMaster complaintMaster){
		this.sessionFactory.getCurrentSession().merge(complaintMaster);
	}
	
	public void saveComplaintTechMapDtls(RlmsComplaintTechMapDtls complaintTechMapDtls){
		this.sessionFactory.getCurrentSession().save(complaintTechMapDtls);
	}
	
	public void saveComplaintSiteVisitDtls(RlmsSiteVisitDtls siteVisitDtls){
		this.sessionFactory.getCurrentSession().save(siteVisitDtls);
	}
	
	@SuppressWarnings("unchecked")
	public List<RlmsComplaintMaster> getAllComplaintsForGivenCriteria(Integer branchCompanyMapId, Integer branchCustomerMapId,List<Integer> listOfLiftCustoMapId,  List<Integer> statusList, Date fromDate, Date toDate,Integer callType){
		 Session session = this.sessionFactory.getCurrentSession();
		 Criteria criteria = session.createCriteria(RlmsComplaintMaster.class);
		 criteria.createAlias("liftCustomerMap.branchCustomerMap", "bcm");
		 criteria.createAlias("bcm.companyBranchMapDtls", "cbm");
				 if(null != branchCompanyMapId){
					 criteria.add(Restrictions.eq("cbm.companyBranchMapId", branchCompanyMapId));
				 }
				 if(null != branchCustomerMapId && !RLMSConstants.MINUS_ONE.getId().equals(branchCustomerMapId)){
					 criteria.add(Restrictions.eq("bcm.branchCustoMapId", branchCustomerMapId));
				 }
				 if(null != listOfLiftCustoMapId && !listOfLiftCustoMapId.isEmpty()){
					 criteria.add(Restrictions.in("liftCustomerMap.liftCustomerMapId", listOfLiftCustoMapId));
				 }
				 if(null != fromDate && null != toDate){
					 criteria.add(Restrictions.ge("registrationDate", fromDate));
					 criteria.add(Restrictions.le("registrationDate", toDate));
				 }
				 if(null != statusList && !statusList.isEmpty()){
					 criteria.add(Restrictions.in("status", statusList));
				 }
				 if(null != callType){
					 criteria.add(Restrictions.eq("callType", callType));
				 }
				 criteria.add(Restrictions.eq("activeFlag", RLMSConstants.ACTIVE.getId()));
				// criteria.add(Order.("registrationDate"));
				 
				 criteria.addOrder(Order.desc("registrationDate"));
				 
				 
		 List<RlmsComplaintMaster> listOfAllcomplaints = criteria.list();
		 return listOfAllcomplaints;
	}
	
	@SuppressWarnings("unchecked")
	public RlmsComplaintTechMapDtls getComplTechMapObjByComplaintId(Integer complaintId){
		 Session session = this.sessionFactory.getCurrentSession();
		 Criteria criteria = session.createCriteria(RlmsComplaintTechMapDtls.class)
				 .add(Restrictions.eq("complaintMaster.complaintId", complaintId));
		 RlmsComplaintTechMapDtls complaintMapDtls = (RlmsComplaintTechMapDtls) criteria.uniqueResult();
		 return complaintMapDtls;
	}
	
	public RlmsComplaintTechMapDtls getComplTechMapByComplaintTechMapId(Integer complaintTechMapId){
		 Session session = this.sessionFactory.getCurrentSession();
		 Criteria criteria = session.createCriteria(RlmsComplaintTechMapDtls.class)
				 .add(Restrictions.eq("complaintTechMapId", complaintTechMapId));
		 RlmsComplaintTechMapDtls complaintMapDtls = (RlmsComplaintTechMapDtls) criteria.uniqueResult();
		 return complaintMapDtls;
	}
	
	
	public RlmsComplaintTechMapDtls getComplTechMapByComplaintId(Integer complaintId){
		RlmsComplaintTechMapDtls complaintMapDtls = null;
		Session session = this.sessionFactory.getCurrentSession();
		 Criteria criteria = session.createCriteria(RlmsComplaintTechMapDtls.class)
				 .add(Restrictions.eq("complaintMaster.complaintId", complaintId));
		 List<RlmsComplaintTechMapDtls> listOfComplaintMapDtls =  criteria.list();
		 if(null != listOfComplaintMapDtls && !listOfComplaintMapDtls.isEmpty()){
			 complaintMapDtls = listOfComplaintMapDtls.get(0);
		 }
		 return complaintMapDtls;
	}
	
	public RlmsComplaintMaster getComplaintMasterObj(Integer complaintId, Integer callType){
		 Session session = this.sessionFactory.getCurrentSession();
		 Criteria criteria = session.createCriteria(RlmsComplaintMaster.class)
				 .add(Restrictions.eq("complaintId", complaintId))
				 .add(Restrictions.eq("activeFlag", RLMSConstants.ACTIVE.getId()));
	/*	 if(null != callType && callType!=0){
			criteria.add(Restrictions.eq("callType", callType));
			// criteria.add(Restrictions.eq("callType", 1));
		 }*/
		 RlmsComplaintMaster complaintMaster = (RlmsComplaintMaster) criteria.uniqueResult();
		 return complaintMaster;
	}
	@SuppressWarnings("unchecked")
	public List<RlmsComplaintMaster> getAllComplaintsByMemberId(Integer memberId,Integer callType){
		 Session session = this.sessionFactory.getCurrentSession();
		 Criteria criteria = session.createCriteria(RlmsComplaintMaster.class)
				 .add(Restrictions.eq("createdBy", memberId))
				 .add(Restrictions.eq("activeFlag", RLMSConstants.ACTIVE.getId()));
		 if(null != callType){
			 criteria.add(Restrictions.eq("callType", callType));
		 }
		 List<RlmsComplaintMaster> listOfAllComplaints =  criteria.list();
		 return listOfAllComplaints;
	}
	
	@SuppressWarnings("unchecked")
	public List<RlmsComplaintTechMapDtls> getListOfComplaintDtlsForTechies(SiteVisitReportDto dto){
 		Session session = this.sessionFactory.getCurrentSession();
		 Criteria criteria = session.createCriteria(RlmsComplaintTechMapDtls.class);
		 criteria.createAlias("complaintMaster.liftCustomerMap", "lcm");
		 criteria.createAlias("lcm.branchCustomerMap", "bcm");
		 criteria.createAlias("bcm.companyBranchMapDtls", "cbm");
		 criteria.createAlias("userRoles", "role");
		 criteria.createAlias("complaintMaster", "ccm");
		 
				 if(null != dto.getCompanyBranchMapId()){
					 criteria.add(Restrictions.eq("cbm.companyBranchMapId", dto.getCompanyBranchMapId()));
				 }
				 if(null != dto.getListOfBranchCustoMapIds() && !dto.getListOfBranchCustoMapIds().isEmpty()){
					 criteria.add(Restrictions.in("bcm.branchCustoMapId", dto.getListOfBranchCustoMapIds()));
				 }
				 if(null != dto.getListOfUserRoleIds()){
					 criteria.add(Restrictions.in("role.userRoleId", dto.getListOfUserRoleIds())); 
				 }
				 if(null != dto.getFromDate() && null != dto.getToDate()){
					 criteria.add(Restrictions.ge("registrationDate", dto.getFromDate()));
					 criteria.add(Restrictions.le("registrationDate", dto.getToDate()));
				 }
				 if(null != dto.getListOfStatusIds() && !dto.getListOfStatusIds().isEmpty()){
					 criteria.add(Restrictions.in("status", dto.getListOfStatusIds()));
				 }
			//	 criteria.add(Restrictions.eq("ccm.callType", 2));
				 
				 criteria.add(Restrictions.ne("status", Status.ASSIGNED.getStatusId()));
				 
				 criteria.add(Restrictions.eq("activeFlag", RLMSConstants.ACTIVE.getId()));
				 
				 criteria.addOrder(Order.desc("ccm.complaintId"));
				 
		 List<RlmsComplaintTechMapDtls> listOfAllcomplaints = criteria.list();
		 return listOfAllcomplaints;
	}
	@SuppressWarnings("unchecked")
	public List<RlmsSiteVisitDtls> getAllVisitsForComnplaints(Integer complaintTechMapId){
		 Session session = this.sessionFactory.getCurrentSession();
		 Criteria criteria = session.createCriteria(RlmsSiteVisitDtls.class)
				 .add(Restrictions.eq("complaintTechMapDtls.complaintTechMapId", complaintTechMapId));
			   	 criteria.addOrder(Order.desc("updatedDate"));
			   	 List<RlmsSiteVisitDtls> listOFAllVisits =  criteria.list();
		 return listOFAllVisits;
	}
	@SuppressWarnings("unchecked")
	public List<RlmsComplaintTechMapDtls> getListOfComplaintDtlsForTechies(TechnicianWiseReportDTO dto){
		Session session = this.sessionFactory.getCurrentSession();
		 Criteria criteria = session.createCriteria(RlmsComplaintTechMapDtls.class);
		 criteria.createAlias("complaintMaster.liftCustomerMap", "lcm");
		 criteria.createAlias("lcm.branchCustomerMap", "bcm");
		 criteria.createAlias("bcm.companyBranchMapDtls", "cbm");
		 criteria.createAlias("cbm.rlmsCompanyMaster", "cm");
		 criteria.createAlias("userRoles", "role");
				 if(null != dto.getBranchCompanyMapId()){
					 criteria.add(Restrictions.eq("cbm.companyBranchMapId", dto.getBranchCompanyMapId()));
				 }
				 if(null != dto.getCompanyId()){
					 criteria.add(Restrictions.eq("cm.companyId", dto.getCompanyId()));
				 }
				
				 if(null != dto.getListOfUserRoleIds()){
					 criteria.add(Restrictions.in("role.userRoleId", dto.getListOfUserRoleIds())); 
				 }
				 
				 if(null != dto.getListOFRatings()){
					 criteria.add(Restrictions.in("userRating", dto.getListOFRatings())); 
				 }
				 
				 criteria.add(Restrictions.eq("activeFlag", RLMSConstants.ACTIVE.getId()));
		 List<RlmsComplaintTechMapDtls> listOfAllcomplaints = criteria.list();
		 return listOfAllcomplaints;
	}

	@Override
	public void updateComplaints(RlmsComplaintTechMapDtls complaintTechMapDtls) {
		this.sessionFactory.getCurrentSession().update(complaintTechMapDtls);
	}

	@Override
	public void updateComplaintsMatser(RlmsComplaintMaster complaintMaster) {
		this.sessionFactory.getCurrentSession().update(complaintMaster);
	}
	
	@Override
	public void deleteComplaintsTechMap(Integer complaintsTechMapId){
		Query q = this.sessionFactory.getCurrentSession().createQuery("delete RlmsComplaintTechMapDtls where complaintTechMapId=:complaintTechMapId");
		q.setParameter("complaintTechMapId", complaintsTechMapId);
		q.executeUpdate();
	}

	@Override
	public List<RlmsComplaintMaster>complaintMastersList (List<Integer> liftCustomerMapIds,ComplaintsDtlsDto dto){

		Session session = this.sessionFactory.getCurrentSession();
		Criteria criteria = session.createCriteria(RlmsComplaintMaster.class);
		criteria.add(Restrictions.in("liftCustomerMap.liftCustomerMapId",liftCustomerMapIds));
		if(dto.getServiceCallType()!=null) {
			criteria.add(Restrictions.eq("callType", dto.getCallType()));
		}
		if(dto.getToDate()!=null && dto.getFromDate()!=null) {
			criteria.add(Restrictions.between("registrationDate", dto.getToDate(),dto.getFromDate()));
		}
		
		 criteria.addOrder(Order.desc("registrationDate"))
		 .add(Restrictions.eq("activeFlag", RLMSConstants.ACTIVE.getId()));

		 List<RlmsComplaintMaster> complaintList= criteria.list();
	     return complaintList;
	}
	@Override
	public RlmsComplaintMaster getComplaintMasterByComplaintId(int complaintId) {
		Session session = this.sessionFactory.getCurrentSession();
		 Criteria criteria = session.createCriteria(RlmsComplaintMaster.class)
				 .add(Restrictions.eq("complaintId", complaintId))
		         .add(Restrictions.eq("activeFlag", RLMSConstants.ACTIVE.getId()));
       	 return  (RlmsComplaintMaster) criteria.uniqueResult();
	}

	@Override
	public RlmsComplaintMaster getComplaintByLiftCustoMapIdAndCallType(ComplaintsDtlsDto complaintsDtlsDto) {
		Session session = this.sessionFactory.getCurrentSession();
		 Criteria criteria = session.createCriteria(RlmsComplaintMaster.class)
				 .add(Restrictions.eq("liftCustomerMap.liftCustomerMapId", complaintsDtlsDto.getLiftCustomerMapId()))
				.add(Restrictions.eq("callType",complaintsDtlsDto.getCallType()))	 
		        .add(Restrictions.eq("activeFlag", RLMSConstants.ACTIVE.getId()));
      	 return  (RlmsComplaintMaster) criteria.uniqueResult();
		
	}
}
