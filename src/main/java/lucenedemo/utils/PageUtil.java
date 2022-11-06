package lucenedemo.utils;

import lucenedemo.dao.IBaseDao;

import lucenedemo.entity.PageData;
import java.lang.reflect.*;
import java.util.*;

public class PageUtil {

    public static <E> PageData<E> getPageData(E entity, IBaseDao<E> dao) {

        List<String> orderData = null;
        Integer currentPage = null;
        Class<?> entityClass = entity.getClass();

        try {
            Field orderDataFiled = entityClass.getSuperclass().getDeclaredField("orderData");
            orderDataFiled.setAccessible(true);
            orderData = CastUtil.cast(orderDataFiled.get(entity));

            Field currentPageFiled = entityClass.getSuperclass().getDeclaredField("currentPage");
            currentPageFiled.setAccessible(true);
            currentPage = CastUtil.cast(currentPageFiled.get(entity));
        } catch (Exception e) {
            e.printStackTrace();
        }

        String orderStr = "";

        if (orderData != null) {
            for (int i = 0; i < orderData.size(); i++) {
                if (i == orderData.size() - 1) {
                    orderStr += orderData.get(i);
                    break;
                }
                orderStr += orderData.get(i) + ",";
            }
        }


		Integer start = (currentPage - 1) * 10;



        Integer totalPage = 1;

        try {
            initEntity(entity, entityClass, orderStr, start);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }


        // 获取总个数
        Integer totalCount = dao.likeSelectCount(entity).intValue();

        List<E> resultList = dao.likeSelect(entity);

        if (totalCount != 0) {

            if (totalCount % 10 == 0) {
                totalPage = totalCount / 10;
            } else {
                totalPage = totalCount / 10 + 1;
            }

        }

        PageData<E> pageData = new PageData<>();

        // 当前页
        pageData.setCurrentPage(currentPage);

        pageData.setCount(totalCount);

        pageData.setTotalPage(totalPage);

        pageData.setResult(resultList);

        return pageData;
    }



	private static <E> void initEntity(E entity, Class<?> entityClass, String orderStr, Integer start) throws NoSuchFieldException, IllegalAccessException {
        	Field startFiled = entityClass.getSuperclass().getDeclaredField("start");
            startFiled.setAccessible(true);
            // sql中的start
            startFiled.set(entity,start);

            Field pageSizeFiled = entityClass.getSuperclass().getDeclaredField("pageSize");
            pageSizeFiled.setAccessible(true);
            // 每页显示10条
            pageSizeFiled.set(entity,10);
            Field orderStrFiled = entityClass.getSuperclass().getDeclaredField("orderStr");
            orderStrFiled.setAccessible(true);
            //排序条件
            orderStrFiled.set(entity, orderStr);
    }

}

