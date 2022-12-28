import api.ProductService;
import db.model.Products;
import lombok.SneakyThrows;
import okhttp3.ResponseBody;
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

public class GetProductsTest {

    static ProductService productService;

    @BeforeAll
    static void beforeAll() {
        productService = RetrofitUtils.getRetrofit().create(ProductService.class);
    }

    @SneakyThrows
    @Test
    void getProductsTest() {
        Response<ResponseBody> responce = productService.getProducts().execute();

        assertThat(responce.isSuccessful(), CoreMatchers.is(true));
        assertThat(responce.code(), equalTo(200));

        db.dao.ProductsMapper productsMapper = SqlSessionUtils.getSession().getMapper(db.dao.ProductsMapper.class);
        db.model.ProductsExample example = new db.model.ProductsExample();

        List<Products> list = productsMapper.selectByExample(example);

        assertThat(productsMapper.countByExample(example), equalTo(5L));
    }

    @AfterAll
    static void afterAll() {
        SqlSessionUtils.getSession().close();
    }
}
