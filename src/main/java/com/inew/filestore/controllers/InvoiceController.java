package com.inew.filestore.controllers;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.mongodb.client.gridfs.model.GridFSUploadOptions;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Nonnull;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static com.mongodb.client.model.Filters.eq;
/**
 * @author Tibor Kulcsar
 * <p>
 * Date: 4/7/2018
 * @since
 */
@Controller
@RequestMapping("invoices")
public class InvoiceController
{
	private final MongoClient mongo;

	@Value("${fstore.mongo.database}")
	private String dbName;

	private LoadingCache<String, GridFSBucket> buckets;


	@Autowired
	public InvoiceController(MongoClient mongo)
	{
		this.mongo = mongo;
		initializeCaches();
	}

	@RequestMapping(path = "/buckets", method = RequestMethod.GET)
	public @ResponseBody List<String> listBuckets(){
		List<String> bucketNames = new ArrayList<>();
		Set<String> colls = new HashSet<>();

		MongoDatabase db = mongo.getDatabase(dbName);

		db.listCollectionNames().forEach((Block<String>) s -> colls.add(s));

		for(String collName : colls) {
			if(collName.endsWith(".chunks")) {
				String potentialBucketName = collName.substring(0,collName.indexOf(".chunks"));
				if(colls.contains(potentialBucketName+".files")) {
					bucketNames.add(potentialBucketName + " is a bucket");
				}
			}
		}
		return bucketNames;
	}

	@RequestMapping(path = "/{provider:\\d+}", method = RequestMethod.GET)
	public @ResponseBody
	List<String> list(@PathVariable("provider") String provider) {
		return getFiles(provider);
	}

	private List<String> getFiles(String provider) {
		List<String> files = new ArrayList<>();
		try
		{
			GridFSBucket bucket = buckets.get(provider);
			bucket.find(eq("metadata.provider", provider)).forEach((Block<GridFSFile>) gridFSFile -> files.add(gridFSFile.getFilename()));

		}
		catch (ExecutionException e)
		{
			e.printStackTrace();
		}
		return files;
	}

	@RequestMapping(method = RequestMethod.PUT)
	public HttpEntity<byte[]> createWithPut(HttpEntity<byte[]> requestEntity)
	{
		byte[] payload = requestEntity.getBody();
		InputStream logo = new ByteArrayInputStream(payload);
		HttpHeaders headers = requestEntity.getHeaders();

		String resp = "<script>window.location = '/upload.html';</script>";
		return new HttpEntity<>(resp.getBytes());
	}

	@RequestMapping(method = RequestMethod.POST)
	public HttpEntity<byte[]> createOrUpdate(@RequestParam("provider") String provider, @RequestParam("file") MultipartFile file) {
		String name = file.getOriginalFilename();

		try
		{
			GridFSBucket bucket = buckets.get(provider);
			bucket.find(eq("filename", name)).forEach((Block<GridFSFile>) gridFSFile -> bucket.delete(gridFSFile.getId()));

			Map<String, Object> meta = new HashMap<>();
			meta.put("contentType", file.getContentType());
			meta.put("length", file.getSize());
			meta.put("provider", provider);

			// Create some custom options
			GridFSUploadOptions options = new GridFSUploadOptions()
					.chunkSizeBytes(1024)
					.metadata(new Document(meta));

			bucket.uploadFromStream(name, file.getInputStream(), options);
			file.getInputStream().close();

			String resp = "<script>window.location = '/upload.html';</script>";
			return new HttpEntity<>(resp.getBytes());
		}
		catch (ExecutionException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return null;
	}


	private void initializeCaches() {
		buckets = CacheBuilder.newBuilder()
				.maximumSize(100)
				.expireAfterWrite(10, TimeUnit.MINUTES)
				.build(
						new CacheLoader<String, GridFSBucket>() {
							@Override
							public GridFSBucket load(@Nonnull String provider) throws Exception {
								MongoDatabase database = mongo.getDatabase(dbName);
								return GridFSBuckets.create(database, provider);
							}
						}
				);
	}

}
