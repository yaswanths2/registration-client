package io.mosip.authentication.common.service.helper;

import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.mosip.authentication.common.service.factory.AuditRequestFactory;
import io.mosip.authentication.common.service.factory.RestRequestFactory;
import io.mosip.authentication.core.constant.AuditEvents;
import io.mosip.authentication.core.constant.AuditModules;
import io.mosip.authentication.core.constant.RestServicesConstants;
import io.mosip.authentication.core.dto.AuditRequestDto;
import io.mosip.authentication.core.dto.RestRequestDTO;
import io.mosip.authentication.core.exception.IDDataValidationException;
import io.mosip.authentication.core.indauth.dto.AuthRequestDTO;
import io.mosip.authentication.core.indauth.dto.IdType;
import io.mosip.authentication.core.spi.indauth.match.IdInfoFetcher;
import io.mosip.kernel.core.http.RequestWrapper;

/**
 * The Class AuditHelper - build audit requests and send it to audit service.
 *
 * @author Manoj SP
 */
@Component
public class AuditHelper {

	/** The rest helper. */
	@Autowired
	private RestHelper restHelper;

	/** The audit factory. */
	@Autowired
	private AuditRequestFactory auditFactory;

	/** The Id Info Fetcher */
	@Autowired
	private IdInfoFetcher idInfoFetcher;

	/** The rest factory. */
	@Autowired
	private RestRequestFactory restFactory;

	public String getUinorVid(AuthRequestDTO authRequestDTO) {
		return Optional.of(idInfoFetcher.getUinOrVid(authRequestDTO).get()).orElse("");
	}

	/**
	 * Method to build audit requests and send it to audit service.
	 *
	 * @param module {@link AuditModules}
	 * @param event  {@link AuditEvents}
	 * @param id     UIN/VID
	 * @param idType {@link IdType}
	 * @param desc   the desc
	 * @throws {@link IDDataValidationException} the ID data validation exception
	 */
	public void audit(AuditModules module, AuditEvents event, String id, IdType idType, String desc)
			throws IDDataValidationException {
		RequestWrapper<AuditRequestDto> auditRequest = auditFactory.buildRequest(module, event, id, idType, desc);
		RestRequestDTO restRequest = restFactory.buildRequest(RestServicesConstants.AUDIT_MANAGER_SERVICE, auditRequest,
				Map.class);
		restHelper.requestAsync(restRequest);
	}

}
