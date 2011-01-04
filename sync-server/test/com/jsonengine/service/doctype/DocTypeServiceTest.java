package com.jsonengine.service.doctype;

import static org.junit.Assert.*;

import org.junit.Test;
import org.slim3.tester.AppEngineTestCase;

public class DocTypeServiceTest extends AppEngineTestCase {

    public static final String TEST_DOCTYPE = "test";

    public static final String TEST_USER_FOO = "foo";

    public static final String TEST_USER_BAR = "bar";

    public static final String DISPLAY_NAME = "display_name";
    
    private DocTypeService service = new DocTypeService();

    @Test
    public void testAccessWithNoDocTypeInfo() throws Exception {
        assertTrue(isAccessibleByPublic(true)); // TODO
        assertTrue(isAccessibleByPublic(false));
    }

    @Test
    public void testAccessForPublic() throws Exception {
        (new DocTypeService()).saveDocTypeInfo(
            TEST_DOCTYPE,
            DocTypeService.ACCESS_LEVEL_PUBLIC,
            DocTypeService.ACCESS_LEVEL_PUBLIC);
        assertTrue(isAccessibleByPublic(true));
        assertTrue(isAccessibleByPublic(false));
    }

    @Test
    public void testAccessForProtected() throws Exception {
        (new DocTypeService()).saveDocTypeInfo(
            TEST_DOCTYPE,
            DocTypeService.ACCESS_LEVEL_PROTECTED,
            DocTypeService.ACCESS_LEVEL_PROTECTED);
        assertFalse(isAccessibleByPublic(true));
        assertFalse(isAccessibleByPublic(false));
        assertTrue(isAccessibleByProtected(true));
        assertTrue(isAccessibleByProtected(false));
        assertTrue(isAccessibleByProtectedWithoutDisplayName(true));
        assertFalse(isAccessibleByProtectedWithoutDisplayName(false));
    }

    @Test
    public void testAccessForPrivate() throws Exception {
        (new DocTypeService()).saveDocTypeInfo(
            TEST_DOCTYPE,
            DocTypeService.ACCESS_LEVEL_PRIVATE,
            DocTypeService.ACCESS_LEVEL_PRIVATE);
        assertFalse(isAccessibleByPublic(true));
        assertFalse(isAccessibleByPublic(false));
        assertFalse(isAccessibleByProtected(true));
        assertFalse(isAccessibleByProtected(false));
        assertTrue(isAccessibleByPrivate(true));
        assertTrue(isAccessibleByPrivate(false));
        assertTrue(isAccessibleByPrivateWithoutDisplayName(true));
        assertFalse(isAccessibleByPrivateWithoutDisplayName(false));
    }

    @Test
    public void testAccessForAdmin() throws Exception {
        (new DocTypeService()).saveDocTypeInfo(
            TEST_DOCTYPE,
            DocTypeService.ACCESS_LEVEL_ADMIN,
            DocTypeService.ACCESS_LEVEL_ADMIN);
        assertFalse(isAccessibleByPublic(true));
        assertFalse(isAccessibleByPublic(false));
        assertFalse(isAccessibleByProtected(true));
        assertFalse(isAccessibleByProtected(false));
        assertFalse(isAccessibleByPrivate(true));
        assertFalse(isAccessibleByPrivate(false));
        assertTrue(isAccessibleByAdmin(true));
        assertTrue(isAccessibleByAdmin(false));
    }

    private boolean isAccessibleByPublic(boolean isRead) {
        return service.isAccessible(
            TEST_DOCTYPE,
            null,
            null,
            TEST_USER_BAR,
            isRead,
            false);
    }

    private boolean isAccessibleByProtected(boolean isRead) {
        return service.isAccessible(
            TEST_DOCTYPE,
            TEST_USER_FOO,
            DISPLAY_NAME ,
            TEST_USER_BAR,
            isRead,
            false);
    }

    private boolean isAccessibleByPrivate(boolean isRead) {
        return service.isAccessible(
            TEST_DOCTYPE,
            TEST_USER_BAR,
            DISPLAY_NAME,
            TEST_USER_BAR,
            isRead,
            false);
    }

    private boolean isAccessibleByProtectedWithoutDisplayName(boolean isRead) {
        return service.isAccessible(
            TEST_DOCTYPE,
            TEST_USER_FOO,
            null,
            TEST_USER_BAR,
            isRead,
            false);
    }

    private boolean isAccessibleByPrivateWithoutDisplayName(boolean isRead) {
        return service.isAccessible(
            TEST_DOCTYPE,
            TEST_USER_BAR,
            null,
            TEST_USER_BAR,
            isRead,
            false);
    }
    
    private boolean isAccessibleByAdmin(boolean isRead) {
        return service.isAccessible(
            TEST_DOCTYPE,
            TEST_USER_BAR,
            null,
            TEST_USER_BAR,
            isRead,
            true);
    }

}
