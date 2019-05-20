<#compress>
<#if (model)??>
{
	"result": {
		<#assign isRetDisplay = "false">
		<#if (model.sys-header)?? && (model.sys-header.RET)??>
			<#list model.sys-header.RET as obj>
				<#if obj_index == 1>
					"retCode": "${obj.RET_CODE}",
					"retMsg": "${obj.RET_MSG?default("")}",
					<#assign isRetDisplay = "true">
					<#break>
				</#if>
			</#list>
		</#if>
		<#if isRetDisplay == "true">
			"retCode": "",
			"retMsg": "",
		</#if>
		"data": {
			"limit": "9999.0",
			"tranDate": "20190419",
			"listDetail": [
				<#if (model.body)?? && (model.body.LIST_DETAIL)??>
					<#list model.body.LIST_DETAIL as obj>
						{
							
							<#if (model.sys - header)?? && (model.sys-header.app-header)?? && (model.sys-header.app-header.APP_HEAD)??>
								"totalNum": ${model.app-header.APP_HEAD.TOTAL_NUM?default(1000)},
							<#else>
								"totalNum": 1000,
							</#if>
							"bankName": "${obj.BANK_NAME?default("")}",
							"bankId": "${obj.ID?default("")}"
						}
						<#if (obj?size != obj_index+1)>
							,
						</#if>
					</#list>
				</#if>
			 ]
		}
	}
}
</#if>
</#compress>
