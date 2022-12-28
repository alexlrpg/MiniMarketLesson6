import api.ProductService;
import com.github.javafaker.Faker;
import db.model.Products;
import dto.Product;
import lombok.SneakyThrows;
import okhttp3.ResponseBody;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import retrofit2.Response;
import util.RetrofitUtils;
import util.SqlSessionUtils;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;

public class ModifyProductTest {

    static ProductService productService;
    Product product;
    Product productCreate;
    Faker faker = new Faker();
    int id;

    @BeforeAll
    static void beforeAll() {
        productService = RetrofitUtils.getRetrofit().create(ProductService.class);
    }

    @SneakyThrows
    @BeforeEach
    void setUp() {
        productCreate = new Product()
                .withTitle(faker.food().ingredient())
                .withCategoryTitle("Food")
                .withPrice((int) (Math.random() * 10000));
        Response<Product> responce = productService.createProduct(productCreate).execute();
        id = responce.body().getId();

        product = new Product()
                .withId(id)
                .withTitle("Lemon")
                .withPrice((int) (Math.random() * 10000))
                .withCategoryTitle("Food");
    }

    @Test
    @SneakyThrows
    void modifyProductTest() {
        Response<Product> responce = productService.modifyProduct(product).execute();
        assertThat(responce.isSuccessful(), CoreMatchers.is(true));

        db.dao.ProductsMapper productsMapper = SqlSessionUtils.getSession().getMapper(db.dao.ProductsMapper.class);
        db.model.ProductsExample example = new db.model.ProductsExample();

        example.createCriteria().andTitleLike("Lemon");
        List<Products> list = productsMapper.selectByExample(example);

        assertThat(list.get(0).getTitle(), CoreMatchers.equalTo("Lemon"));
    }

    @SneakyThrows
    @AfterEach
    void tearDown() {
        Response<ResponseBody> responce = productService.deleteProduct(id).execute();
        assertThat(responce.isSuccessful(), CoreMatchers.is(true));

        SqlSessionUtils.getSession().close();
    }
}
