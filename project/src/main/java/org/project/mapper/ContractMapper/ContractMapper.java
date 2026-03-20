package org.project.mapper.ContractMapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.project.model.contract.Contract;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface ContractMapper extends BaseMapper<Contract> {

	IPage<Contract> selectContractPage(
			Page<Contract> page,
			@Param("unitName") String unitName,
			@Param("keyword") String keyword,
			@Param("contractStatus") Integer contractStatus,
			@Param("startDateBegin") LocalDate startDateBegin,
			@Param("startDateEnd") LocalDate startDateEnd,
			@Param("endDateBegin") LocalDate endDateBegin,
			@Param("endDateEnd") LocalDate endDateEnd
	);

	Contract selectContractDetailById(@Param("contractId") Long contractId);

	List<Contract> selectRenewalChain(@Param("contractId") Long contractId);

	List<Contract> selectExpiringContracts(@Param("days") Integer days);

}
