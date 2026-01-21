package com.phonebook_system.report_service.client;

import com.phonebook_system.report_service.base.BaseResponseModel;
import com.phonebook_system.report_service.model.ContactTypeEnum;
import com.phonebook_system.report_service.model.response.LocationStatisticListResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "contact-service", url = "${app.contact-service.url}")
public interface ContactServiceClient {

    @GetMapping("/api/persons/location-stats")
    BaseResponseModel<LocationStatisticListResponse> getLocationStats(@RequestParam ContactTypeEnum type);
}
