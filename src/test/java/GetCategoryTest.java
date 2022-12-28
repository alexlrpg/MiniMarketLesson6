import api.CategoryService;
import db.model.Categories;
import dto.GetCategoryResponce;
import lombok.SneakyThrows;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import retrofit2.Response;
import util.RetrofitUtils;
import util.SqlSessionUtils;


import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class GetCategoryTest {

    static CategoryService categoryService;

    @BeforeAll
    static void beforeAll() {
        categoryService = RetrofitUtils.getRetrofit().create(CategoryService.class);
    }

    @SneakyThrows
    @Test
    void getCategoryByIdPositiveTest() {
        Response<GetCategoryResponce> responce = categoryService.getCategory(1).execute();
        assertThat(responce.isSuccessful(), CoreMatchers.is(true));
        assertThat(responce.body().getId(), equalTo(1));
        assertThat(responce.body().getTitle(), equalTo("Food"));
        responce.body().getProducts().forEach(product ->
                assertThat(product.getCategoryTitle(), equalTo("Food")));

        db.dao.CategoriesMapper categoriesMapper = SqlSessionUtils.getSession().getMapper(db.dao.CategoriesMapper.class);
        db.model.CategoriesExample example = new db.model.CategoriesExample();

        example.createCriteria().andIdEqualTo(1L);
        List<Categories> list = categoriesMapper.selectByExample(example);

        assertThat(categoriesMapper.countByExample(example), equalTo(1L));
        assertThat(list.get(0).getTitle(), equalTo("Food"));
    }

    @SneakyThrows
    @Test
    void getCategoryByIdNegativeTest() {
        Response<GetCategoryResponce> responce = categoryService.getCategory(3).execute();
        assertThat(responce.isSuccessful(), CoreMatchers.is(false));
        assertThat(responce.code(), equalTo(404));

        db.dao.CategoriesMapper categoriesMapper = SqlSessionUtils.getSession().getMapper(db.dao.CategoriesMapper.class);
        db.model.CategoriesExample example = new db.model.CategoriesExample();

        example.createCriteria().andIdEqualTo(3L);
        List<Categories> list = categoriesMapper.selectByExample(example);

        assertThat(categoriesMapper.countByExample(example), equalTo(0L));
        assertThat(String.valueOf(list.isEmpty()), true);
    }

    @AfterAll
    static void afterAll() {
        SqlSessionUtils.getSession().close();
    }
}
