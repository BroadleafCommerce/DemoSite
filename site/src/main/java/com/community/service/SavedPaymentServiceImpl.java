/*-
 * #%L
 * Community Demo Site
 * %%
 * Copyright (C) 2009 - 2023 Broadleaf Commerce
 * %%
 * Licensed under the Broadleaf Fair Use License Agreement, Version 1.0
 * (the "Fair Use License" located  at http://license.broadleafcommerce.org/fair_use_license-1.0.txt)
 * unless the restrictions on use therein are violated and require payment to Broadleaf in which case
 * the Broadleaf End User License Agreement (EULA), Version 1.1
 * (the "Commercial License" located at http://license.broadleafcommerce.org/commercial_license-1.1.txt)
 * shall apply.
 * 
 * Alternatively, the Commercial License may be replaced with a mutually agreed upon license (the "Custom License")
 * between you and Broadleaf Commerce. You may not use this file except in compliance with the applicable license.
 * #L%
 */
package com.community.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.payment.PaymentAdditionalFieldType;
import org.broadleafcommerce.common.payment.dto.PaymentRequestDTO;
import org.broadleafcommerce.common.payment.dto.PaymentResponseDTO;
import org.broadleafcommerce.common.payment.service.CustomerPaymentGatewayService;
import org.broadleafcommerce.common.payment.service.PaymentGatewayCustomerService;
import org.broadleafcommerce.common.vendor.service.exception.PaymentException;
import org.broadleafcommerce.core.web.checkout.model.PaymentInfoForm;
import org.broadleafcommerce.core.web.payment.service.SavedPaymentService;
import org.broadleafcommerce.payment.service.gateway.SamplePaymentGatewayConfiguration;
import org.broadleafcommerce.profile.core.domain.Address;
import org.broadleafcommerce.profile.core.domain.Customer;
import org.broadleafcommerce.vendor.sample.service.payment.MessageConstants;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author Chris Kittrell (ckittrell)
 */
@Service("blSavedPaymentService")
public class SavedPaymentServiceImpl implements SavedPaymentService {

    private static final Log LOG = LogFactory.getLog(SavedPaymentServiceImpl.class);

    @Resource(name = "blSamplePaymentGatewayCustomerService")
    protected PaymentGatewayCustomerService paymentGatewayCustomerService;

    @Resource(name = "blSamplePaymentGatewayConfiguration")
    protected SamplePaymentGatewayConfiguration configuration;

    @Resource(name = "blCustomerPaymentGatewayService")
    protected CustomerPaymentGatewayService customerPaymentGatewayService;


    public Long addSavedPayment(Customer customer, PaymentInfoForm paymentInfoForm) {
        try {
            PaymentRequestDTO requestDTO = buildPaymentRequestDTO(customer, paymentInfoForm.getPaymentToken());
            PaymentResponseDTO responseDTO = paymentGatewayCustomerService.createGatewayCustomer(requestDTO);

            if (responseDTO.isSuccessful()) {
                addAddressToResponseDTO(responseDTO, paymentInfoForm.getAddress());
                addPaymentNameToResponseDTO(responseDTO, paymentInfoForm.getPaymentName());
                addIsDefaultMethodToResponseDTO(responseDTO, paymentInfoForm.getIsDefault());

                return customerPaymentGatewayService.createCustomerPaymentFromResponseDTO(responseDTO, configuration);
            }
        } catch (PaymentException e) {
            LOG.error("Could not create gateway customer", e);
        }
        return null;
    }

    public Long updateSavedPayment(Customer customer, PaymentInfoForm paymentInfoForm) {
        try {
            PaymentRequestDTO requestDTO = buildPaymentRequestDTO(customer, paymentInfoForm.getPaymentToken());
            PaymentResponseDTO responseDTO = paymentGatewayCustomerService.updateGatewayCustomer(requestDTO);

            if (responseDTO.isSuccessful()) {
                addAddressToResponseDTO(responseDTO, paymentInfoForm.getAddress());
                addPaymentNameToResponseDTO(responseDTO, paymentInfoForm.getPaymentName());
                addIsDefaultMethodToResponseDTO(responseDTO, paymentInfoForm.getIsDefault());

                return customerPaymentGatewayService.updateCustomerPaymentFromResponseDTO(responseDTO, configuration);
            }
        } catch (PaymentException e) {
            LOG.error("Could not create gateway customer", e);
        }
        return null;
    }

    private void addPaymentNameToResponseDTO(PaymentResponseDTO responseDTO, String paymentName) {
        responseDTO.getResponseMap().put(PaymentAdditionalFieldType.PAYMENT_NAME.getType(), paymentName);
    }

    private void addIsDefaultMethodToResponseDTO(PaymentResponseDTO responseDTO, boolean isDefault) {
        responseDTO.getResponseMap().put("isDefault", Boolean.toString(isDefault));
    }

    public void deleteSavedPayment(Customer customer, String nonce) {
        try {
            PaymentRequestDTO requestDTO = buildPaymentRequestDTO(customer, nonce);
            PaymentResponseDTO responseDTO = paymentGatewayCustomerService.deleteGatewayCustomer(requestDTO);

            if (responseDTO.isSuccessful()) {
                customerPaymentGatewayService.deleteCustomerPaymentFromResponseDTO(responseDTO, configuration);
            }
        } catch (PaymentException e) {
            LOG.error("Could not create gateway customer", e);
        }
    }

    protected PaymentRequestDTO buildPaymentRequestDTO(Customer customer, String nonce) {
        return new PaymentRequestDTO()
                .customer()
                    .customerId(customer.getId().toString())
                    .firstName(customer.getFirstName())
                    .lastName(customer.getLastName())
                    .email(customer.getEmailAddress())
                .done()
                .additionalField(MessageConstants.PAYMENT_NONCE , nonce);
    }

    protected void addAddressToResponseDTO(PaymentResponseDTO responseDTO, Address address) {
        responseDTO.billTo()
                .addressFullName(address.getFullName())
                .addressLine1(address.getAddressLine1())
                .addressLine2(address.getAddressLine2())
                .addressCityLocality(address.getCity())
                .addressStateRegion(address.getStateProvinceRegion())
                .addressCountryCode(address.getIsoCountryAlpha2().getAlpha2())
                .addressPostalCode(address.getPostalCode())
                .addressPhone(address.getPhonePrimary().getPhoneNumber());
    }
}
