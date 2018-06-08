package com.rlms.service;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rlms.constants.SpocRoleConstants;
import com.rlms.constants.Status;
import com.rlms.dao.AMCMonitorDao;
import com.rlms.dao.LiftDao;
import com.rlms.dao.UserRoleDao;
import com.rlms.model.RlmsLiftAmcDtls;
import com.rlms.model.RlmsUserRoles;
import com.rlms.utils.DateUtils;

@Service
public class AMCMonitorServiceImpl implements AMCMonitorService{
	
	@Autowired
	AMCMonitorDao amcMonitorDao;
	
	@Autowired
     LiftDao liftDao;
	
	@Autowired
	UserRoleDao userRoleDao;
	
	@Autowired
	MessagingService messagingService;
	
	@Override
	public RlmsLiftAmcDtls getAllAMCDtlsAndUpdateStatus() throws UnsupportedEncodingException {
		 List<RlmsLiftAmcDtls>  amcDtlsList = amcMonitorDao.getAllAMCDetails();
		 for (RlmsLiftAmcDtls rlmsLiftAmcDtls : amcDtlsList) {
			 if(rlmsLiftAmcDtls.getAmcStartDate()!=null && rlmsLiftAmcDtls.getAmcEndDate()!=null) {
				 Date amcStartDate = 	rlmsLiftAmcDtls.getAmcStartDate();
				 Date amcEndDate = rlmsLiftAmcDtls.getAmcEndDate();
				 int status =  calculateAMCStatus(amcStartDate,amcEndDate);
				 rlmsLiftAmcDtls.setStatus(status);
				 liftDao.mergeLiftAMCDtls(rlmsLiftAmcDtls);
				 if(status==Status.getIdFromString(Status.AMC_PENDING.getStatusMsg())||status==Status.getIdFromString(Status.RENEWAL_DUE.getStatusMsg())) {
						List<String> listOfDynamicValues = new ArrayList<String>();
						listOfDynamicValues.add(rlmsLiftAmcDtls.getLiftCustomerMap().getLiftMaster().getLiftNumber());
						listOfDynamicValues.add(rlmsLiftAmcDtls.getLiftCustomerMap().getBranchCustomerMap().getCustomerMaster().getAddress()+ ", " + rlmsLiftAmcDtls.getLiftCustomerMap().getBranchCustomerMap().getCustomerMaster().getArea() + ", " + rlmsLiftAmcDtls.getLiftCustomerMap().getBranchCustomerMap().getCustomerMaster().getCity());
						listOfDynamicValues.add(DateUtils.convertDateToStringWithoutTime(rlmsLiftAmcDtls.getAmcEndDate()));
						
						List<RlmsUserRoles> listOfAdmin = this.userRoleDao.getAllUserWithRoleForBranch(rlmsLiftAmcDtls.getLiftCustomerMap().getBranchCustomerMap().getBranchCustoMapId(), SpocRoleConstants.BRANCH_ADMIN.getSpocRoleId());
						if(null != listOfAdmin && !listOfAdmin.isEmpty()){
							listOfDynamicValues.add(listOfAdmin.get(0).getRlmsUserMaster().getFirstName() + " " + listOfAdmin.get(0).getRlmsUserMaster().getLastName() + " (" + listOfAdmin.get(0).getRlmsUserMaster().getContactNumber() + ")");
						}else{
							RlmsUserRoles companyAdmin = this.userRoleDao.getUserWithRoleForCompany(rlmsLiftAmcDtls.getLiftCustomerMap().getBranchCustomerMap().getCompanyBranchMapDtls().getRlmsCompanyMaster().getCompanyId(), SpocRoleConstants.COMPANY_ADMIN.getSpocRoleId());
							if(null != companyAdmin){
								listOfDynamicValues.add(companyAdmin.getRlmsUserMaster().getFirstName() + " " + companyAdmin.getRlmsUserMaster().getLastName() + " (" + companyAdmin.getRlmsUserMaster().getContactNumber() + ")");
							}
						}
						List<String> toList = new ArrayList<String>();
						toList.add(rlmsLiftAmcDtls.getLiftCustomerMap().getBranchCustomerMap().getCustomerMaster().getEmailID());
					
						if(status==Status.getIdFromString(Status.AMC_PENDING.getStatusMsg())) {
							this.messagingService.sendAMCMail(listOfDynamicValues, toList, com.rlms.constants.EmailTemplateEnum.AMC_EXPIRED.getTemplateId());
						}
						else {
							this.messagingService.sendAMCMail(listOfDynamicValues, toList, com.rlms.constants.EmailTemplateEnum.AMC_RENEWAL.getTemplateId());
						}
				 }
			 }
		}
		return null;
	}
	public int calculateAMCStatus(Date amcStartDate , Date amcEndDate) {
		
		int amcStatus = 0;
		Date today = new Date();
		Date renewalDate = DateUtils.addDaysToDate(amcEndDate, -30);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			today = sdf.parse(sdf.format(new Date()));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		int diff = DateUtils.daysBetween(today,renewalDate);
		if(diff<=30 &&diff>=0) {
		//	if((DateUtils.isAfterOrEqualTo(renewalDate,today)) && (DateUtils.isBeforeOrEqualToDate(today, amcEndDate))){
			amcStatus = Status.RENEWAL_DUE.getStatusId();
		 }
		 else if(DateUtils.isBeforeToDate(amcEndDate, today)){
				amcStatus = Status.AMC_PENDING.getStatusId();
			 }
		 else if((DateUtils.isBeforeOrEqualToDate(amcStartDate,today))&&(DateUtils.isAfterOrEqualTo(today,amcEndDate))){
				amcStatus = Status.UNDER_AMC.getStatusId();
			}
		return amcStatus;
	}
	
//	@Transactional(propagation = Propagation.REQUIRED)
	public void  AMCExpiredstatusNotifyToUser() throws UnsupportedEncodingException{
		List<RlmsLiftAmcDtls> listOfAllLifts = this.liftDao.getAllLiftsWithTodaysExpiryDate();
		for (RlmsLiftAmcDtls rlmsLiftAmcDtls : listOfAllLifts) {
		//	rlmsLiftAmcDtls.setStatus(Status.AMC_PENDING.getStatusId());
		//	this.liftDao.mergeLiftAMCDtls(rlmsLiftAmcDtls);

			List<String> listOfDynamicValues = new ArrayList<String>();
			listOfDynamicValues.add(rlmsLiftAmcDtls.getLiftCustomerMap().getLiftMaster().getLiftNumber());
			listOfDynamicValues.add(rlmsLiftAmcDtls.getLiftCustomerMap().getBranchCustomerMap().getCustomerMaster().getAddress()+ ", " + rlmsLiftAmcDtls.getLiftCustomerMap().getBranchCustomerMap().getCustomerMaster().getArea() + ", " + rlmsLiftAmcDtls.getLiftCustomerMap().getBranchCustomerMap().getCustomerMaster().getCity());
			listOfDynamicValues.add(DateUtils.convertDateToStringWithoutTime(rlmsLiftAmcDtls.getAmcEndDate()));
			
			List<RlmsUserRoles> listOfAdmin = this.userRoleDao.getAllUserWithRoleForBranch(rlmsLiftAmcDtls.getLiftCustomerMap().getBranchCustomerMap().getBranchCustoMapId(), SpocRoleConstants.BRANCH_ADMIN.getSpocRoleId());
			if(null != listOfAdmin && !listOfAdmin.isEmpty()){
				listOfDynamicValues.add(listOfAdmin.get(0).getRlmsUserMaster().getFirstName() + " " + listOfAdmin.get(0).getRlmsUserMaster().getLastName() + " (" + listOfAdmin.get(0).getRlmsUserMaster().getContactNumber() + ")");
			}else{
				RlmsUserRoles companyAdmin = this.userRoleDao.getUserWithRoleForCompany(rlmsLiftAmcDtls.getLiftCustomerMap().getBranchCustomerMap().getCompanyBranchMapDtls().getRlmsCompanyMaster().getCompanyId(), SpocRoleConstants.COMPANY_ADMIN.getSpocRoleId());
				if(null != companyAdmin){
					listOfDynamicValues.add(companyAdmin.getRlmsUserMaster().getFirstName() + " " + companyAdmin.getRlmsUserMaster().getLastName() + " (" + companyAdmin.getRlmsUserMaster().getContactNumber() + ")");
				}
			}
			List<String> toList = new ArrayList<String>();
			toList.add(rlmsLiftAmcDtls.getLiftCustomerMap().getBranchCustomerMap().getCustomerMaster().getEmailID());
			this.messagingService.sendAMCMail(listOfDynamicValues, toList, com.rlms.constants.EmailTemplateEnum.AMC_EXPIRED.getTemplateId());
		}
	}
}
