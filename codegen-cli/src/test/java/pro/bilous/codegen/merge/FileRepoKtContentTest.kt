package pro.bilous.codegen.merge

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class FileRepoKtContentTest {

	@Test
	fun `should pass test and merge content`() {
		val fileMerge = FileMerge()
		val fakeFilePath = "/path/to/file/UserApplication.kt"
		val result = fileMerge.mergeFileContent(testKtExisting, testKtNew, fakeFilePath)

		assertEquals(expectedKtResult, result)
	}

	private val testKtExisting = """
	package com.flo.rater.account.repository

	import com.flo.rater.account.domain.CustomerAccount
	import com.flo.rater.repository.CommonRepository
	import org.springframework.data.domain.Pageable
	import org.springframework.data.jpa.repository.Query
	import org.springframework.stereotype.Repository
	import java.time.LocalDate
	import java.util.*

	@javax.annotation.Generated(value = ["org.openapitools.codegen.CodeCodegen"])

	@Repository
	//codegen:merge
	interface CustomerAccountsRepository : CommonRepository<CustomerAccount> {

		fun findByParentId(parentId: UUID): Optional<CustomerAccount>

		@Query(""${'"'}
			select ca from CustomerAccount ca
				where ca.billingCyclePeriodType is not null and
					ca.isDeleted = false and ca.nextBillingRenewal <= :date
		""${'"'})
		fun getForBillingCycle(date: LocalDate, pageable: Pageable): List<CustomerAccount>

		@Query("SELECT ca.id FROM CustomerAccount ca")
		fun findAllId(): Iterable<UUID>
	}

	"""

	private val testKtNew = """
	package com.flo.rater.account.repository

	import com.flo.rater.account.domain.CustomerAccount
	import com.flo.rater.repository.CommonRepository
	import org.springframework.stereotype.Repository
	import java.util.*

	@javax.annotation.Generated(value = ["org.openapitools.codegen.CodeCodegen"])

	@Repository
	interface CustomerAccountsRepository : CommonRepository<CustomerAccount>

	"""

	private val expectedKtResult = """
	package com.flo.rater.account.repository

	import com.flo.rater.account.domain.CustomerAccount
	import com.flo.rater.repository.CommonRepository
	import org.springframework.stereotype.Repository
	import java.util.*

	@javax.annotation.Generated(value = ["org.openapitools.codegen.CodeCodegen"])

	@Repository
	//codegen:merge
	interface CustomerAccountsRepository : CommonRepository<CustomerAccount> {

		fun findByParentId(parentId: UUID): Optional<CustomerAccount>

		@Query(""${'"'}
			select ca from CustomerAccount ca
				where ca.billingCyclePeriodType is not null and
					ca.isDeleted = false and ca.nextBillingRenewal <= :date
		""${'"'})
		fun getForBillingCycle(date: LocalDate, pageable: Pageable): List<CustomerAccount>

		@Query("SELECT ca.id FROM CustomerAccount ca")
		fun findAllId(): Iterable<UUID>
	}

	"""
}
