package com.jt.common.mapper;

import static org.apache.ibatis.jdbc.SqlBuilder.BEGIN;
import static org.apache.ibatis.jdbc.SqlBuilder.DELETE_FROM;
import static org.apache.ibatis.jdbc.SqlBuilder.SQL;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.persistence.Table;

import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.scripting.xmltags.ForEachSqlNode;
import org.apache.ibatis.scripting.xmltags.MixedSqlNode;
import org.apache.ibatis.scripting.xmltags.SqlNode;
import org.apache.ibatis.scripting.xmltags.StaticTextSqlNode;

import com.github.abel533.mapper.MapperProvider;
import com.github.abel533.mapperhelper.EntityHelper;
import com.github.abel533.mapperhelper.MapperHelper;

public class SysMapperProvider extends MapperProvider {

    public SysMapperProvider(Class<?> mapperClass, MapperHelper mapperHelper) {
        super(mapperClass, mapperHelper);
    }

    public SqlNode deleteByIDS(MappedStatement ms) {
        Class<?> entityClass = getSelectReturnType(ms);
        Set<EntityHelper.EntityColumn> entityColumns = EntityHelper.getPKColumns(entityClass);
        EntityHelper.EntityColumn column = null;
        for (EntityHelper.EntityColumn entityColumn : entityColumns) {
            column = entityColumn;
            break;
        }
        
        List<SqlNode> sqlNodes = new ArrayList<SqlNode>();
        // 开始拼sql
        BEGIN();
        // delete from table
        DELETE_FROM(tableName(entityClass));
        // 得到sql
        String sql = SQL();
        // 静态SQL部分
        sqlNodes.add(new StaticTextSqlNode(sql + " WHERE " + column.getColumn() + " IN "));
        // 构造foreach sql
        SqlNode foreach = new ForEachSqlNode(ms.getConfiguration(), new StaticTextSqlNode("#{"
                + column.getProperty() + "}"), "ids", "index", column.getProperty(), "(", ")", ",");
        sqlNodes.add(foreach);
        return new MixedSqlNode(sqlNodes);
    }
    
    
    /**
     * 通用Mapper的测试用例
     * 1.SqlNode Mybatis通过sqlNode转化为sql语句
     * 2.MappedStatement 通过Mapper的内置对象,通过对象获取想要的参数
     * 
     * 1.获取方法调用的路径
     * 2.截取字串,形成ItemMapper路径
     * 3.通过反射  获取ItemMapper的Class类型
     * 4.获取父级Mapper接口
     * 5.判断接口是否为泛型   
     * 6.获取泛型的参数
     * 7.转化为Class类型    itemClass
     * 8.判断是否还有注解      
     * 9.获取注解中的表名
     * 10.拼接Sql语句 转化为SqlNode交给Mybatis解析
     * @throws ClassNotFoundException 
     * 
     */
    public SqlNode findCountTable(MappedStatement ms) throws ClassNotFoundException{
    	//1.获取调用方法的全路径  com.jt.manage.mapper.ItemMapper.findCountTable()
    	String path = ms.getId();
    	
    	//2.获取ItemMapper的全路径
    	String targetPath = path.substring(0,path.lastIndexOf("."));
    	
    	//3.获取MapperClass类型
    	Class<?> targetClass = Class.forName(targetPath);
    	
    	//4.获取ItemMapper继承的全部父级接口      Class的接口Type 是一个超类
    	Type[]  types  =  targetClass.getGenericInterfaces();
    	
    	//5.获取父级接口的Type类型
    	Type superType = types[0];
    	
    	//6.如何判定当前的superType就是一个泛型           ParameterizedType=泛型
    	if(superType instanceof ParameterizedType){
    		//7.将Type类型转化为泛型类型
    		ParameterizedType pType =  (ParameterizedType) superType;
    		
    		//8.获取泛型的参数Item
    		Type[] pojoType =  pType.getActualTypeArguments();
    		
    		//9.获取泛型Class ItemClass类型
    		Class pojoClass = (Class) pojoType[0];
    		
    		//10.判断当前class类型是否含有table注解
    		if(pojoClass.isAnnotationPresent(Table.class)){
    			
    			//11.获取注解
    			Table table = (Table) pojoClass.getAnnotation(Table.class);
    			
    			//12.获取表名
    			String tableName = table.name();
    			
    			//13.拼接Sql语句
    			String sql = "select count(*) from " +tableName;
    			
    			//14.创建SqlNode对象
    			SqlNode sqlNode = new StaticTextSqlNode(sql);
    			
    			return sqlNode;
    		}
    	}
    	return null;
    }
    
    
    
    
    
    
    

}
