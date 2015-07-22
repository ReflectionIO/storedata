package io.reflection.app.helpers;

import static org.junit.Assert.*;
import io.reflection.app.helpers.GoogleCloudClientHelper;

import java.util.AbstractMap.SimpleEntry;

import org.junit.Test;

public class GoogleCloudClientHelperTest {

	@Test
	public void getGCSBucketAndFileNameTest() {
		SimpleEntry<String, String> bucketAndFileName = GoogleCloudClientHelper.getGCSBucketAndFileName("/gs/gather/ios/vg/6011/topfreeipadapplications_1648");
		assertNotNull(bucketAndFileName);
		assertEquals("gather", bucketAndFileName.getKey());
		assertEquals("ios/vg/6011/topfreeipadapplications_1648", bucketAndFileName.getValue());
	}
}
