package ${basePackage}.services.impl;

import ${basePackage}.daos.${modelNameUpperCamel}Mapper;
import ${basePackage}.beans.${modelNameUpperCamel};
import ${basePackage}.services.${modelNameUpperCamel}Service;
import ${basePackage}.core.sservice.AbstractService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;


/**
\* User: ${author}
\* Date: ${date}
\* To change this template use File | Settings | File Templates.
\* Description:
\*/

@Service("${modelNameLowerCamel}Service")
@Transactional
public class ${modelNameUpperCamel}ServiceImpl extends AbstractService<${modelNameUpperCamel}> implements ${modelNameUpperCamel}Service {
    @Autowired
    private ${modelNameUpperCamel}Mapper ${modelNameLowerCamel}Mapper;

}
