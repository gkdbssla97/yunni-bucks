package sejong.coffee.yun.integration.menu;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.ResultActions;
import sejong.coffee.yun.integration.MainIntegrationTest;
import sejong.coffee.yun.repository.thumbnail.ThumbNailRepository;
import sejong.coffee.yun.service.MenuThumbNailService;

import java.io.FileInputStream;
import java.time.LocalDateTime;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class MenuThumbnailIntegrationTest extends MainIntegrationTest {

    @Autowired
    private MenuThumbNailService menuThumbNailService;
    @Autowired
    private ThumbNailRepository thumbNailRepository;

    @AfterEach
    void initDB() {
        thumbNailRepository.clear();
    }

    @Nested
    @DisplayName("썸네일을 업로드한다.")
    @Sql(value = {"/sql/user.sql", "/sql/menu.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/truncate.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    class Upload {

        MockMultipartFile multipartFile;

        @BeforeEach
        void init() throws Exception {
            String name = "image";
            String originalFileName = "test.jpeg";
            String fileUrl = "/Users/yungwang-o/Documents/test.jpeg";

            multipartFile = new MockMultipartFile(
                    name,
                    originalFileName,
                    MediaType.IMAGE_JPEG_VALUE,
                    new FileInputStream(fileUrl));
        }

        @Test
        void 썸네일_업로드_204() throws Exception {
            // given

            // when
            ResultActions resultActions = mockMvc.perform(multipart(MENU_THUMBNAIL_API_PATH + "/{menuId}/thumbnails", 1L)
                    .file(multipartFile));

            // then
            resultActions.andExpect(status().isNoContent())
                    .andDo(document("thumbnail-upload",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            pathParameters(
                                    parameterWithName("menuId").description("메뉴 ID")
                            ),
                            requestParts(
                                    partWithName("image").description("썸네일 이미지")
                            )
                    ));
        }

        @Test
        void 메뉴에_업로드_된_이미지_가져오기_200() throws Exception {
            // given
            menuThumbNailService.create(multipartFile, 1L, LocalDateTime.now());

            // when
            ResultActions resultActions = mockMvc.perform(get(MENU_THUMBNAIL_API_PATH + "/{menuId}/thumbnails", 1L));

            // then
            resultActions.andExpect(status().isOk())
                    .andDo(print());
        }
    }
}
