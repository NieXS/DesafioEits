package com.stockontrol.domain.service;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.directwebremoting.annotations.RemoteMethod;
import org.directwebremoting.annotations.RemoteProxy;
import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.stockontrol.domain.entity.Batch;
import com.stockontrol.domain.entity.QBatch;
import com.stockontrol.domain.repository.BatchRepository;
import com.stockontrol.domain.util.PredicateList;
import com.stockontrol.domain.util.SimplePageRequest;

@Service("batchService")
@Transactional
@RemoteProxy(name = "batchService")
public class BatchService
{
	@Autowired
	private BatchRepository batchRepository;
	@Autowired
	private UserService userService;
	@Autowired
	private Clock clock;

	@PreAuthorize("hasRole('USER')")
	@RemoteMethod
	public Batch insert(Batch batch)
	{
		Assert.assertNull("Lote já existe!", batch.getId());
		batch.setUser(userService.getCurrent());
		return batchRepository.saveAndFlush(batch);
	}

	@PreAuthorize("hasRole('USER')")
	@RemoteMethod
	public Batch find(Long id)
	{
		return batchRepository.findOne(id);
	}

	@PreAuthorize("hasRole('USER')")
	@RemoteMethod
	public Page<Batch> listAllByFilters(String filter, LocalDate maxExpirationDate,
			Long productId, SimplePageRequest page)
	{
		QBatch batch = QBatch.batch;
		PredicateList predicates = new PredicateList();
		predicates.add(filter, () -> batch.product.name.containsIgnoreCase(filter).or(batch.identifier.containsIgnoreCase(filter)))
				.add(maxExpirationDate, () -> batch.expiresAt.loe(maxExpirationDate))
				.add(productId, () -> batch.product.id.eq(productId));
		return batchRepository.findAll(predicates.getIntersection(), SimplePageRequest.toPageRequest(page));
	}

	@PreAuthorize("hasRole('USER')")
	@RemoteMethod
	public Page<Batch> listAllExpired(Long productId, SimplePageRequest page)
	{
		return batchRepository.listAllExpiringBefore(productId, LocalDateTime.now(clock),
				SimplePageRequest.toPageRequest(page));
	}

	@PreAuthorize("hasRole('USER')")
	@RemoteMethod
	public Page<Batch> listAllExpiring(Long productId, SimplePageRequest page)
	{
		return batchRepository.listAllExpiringAfter(productId, LocalDateTime.now(clock),
				SimplePageRequest.toPageRequest(page));
	}

	@PreAuthorize("hasRole('USER')")
	@RemoteMethod
	public Batch registerOutgoingById(Long id, int quantity)
	{
		Batch batch = find(id);
		Assert.assertNotNull(String.format("Lote com id %d não existe", id), batch);
		Assert.assertTrue(String.format("Não é possível remover %d itens do lote id %d com %d itens", quantity,
				batch.getId(), batch.getQuantity()), batch.getQuantity() >= quantity);
		if (quantity == batch.getQuantity())
		{
			batchRepository.delete(batch);
			return null;
		} else
		{
			batch.setQuantity(batch.getQuantity() - quantity);
			batch = batchRepository.saveAndFlush(batch);
		}
		return batch;
	}
}
