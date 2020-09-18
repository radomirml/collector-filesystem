/* Copyright 2013-2017 Norconex Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.norconex.collector.fs.crawler;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLStreamException;

import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.norconex.collector.core.checksum.IMetadataChecksummer;
import com.norconex.collector.core.crawler.AbstractCrawlerConfig;
import com.norconex.collector.fs.checksum.impl.FileMetadataChecksummer;
import com.norconex.collector.fs.doc.IFileDocumentProcessor;
import com.norconex.collector.fs.fetch.IFileDocumentFetcher;
import com.norconex.collector.fs.fetch.IFileMetadataFetcher;
import com.norconex.collector.fs.fetch.impl.GenericFileDocumentFetcher;
import com.norconex.collector.fs.fetch.impl.GenericFileMetadataFetcher;
import com.norconex.collector.fs.option.IFilesystemOptionsProvider;
import com.norconex.collector.fs.option.impl.GenericFilesystemOptionsProvider;
import com.norconex.commons.lang.config.XMLConfigurationUtil;
import com.norconex.commons.lang.xml.EnhancedXMLStreamWriter;

/**
 * Filesystem Crawler configuration.
 * 
 * @author Pascal Essiembre
 */
public class FilesystemCrawlerConfig extends AbstractCrawlerConfig {

    private static final Logger LOG = 
            LogManager.getLogger(FilesystemCrawlerConfig.class);
    
    private String[] startPaths;
    private String[] pathsFiles;
    private IStartPathsProvider[] startPathsProviders;
    
    private boolean keepDownloads;
    private boolean skipDocumentFetch;

    private IFilesystemOptionsProvider optionsProvider = 
            new GenericFilesystemOptionsProvider();

    private IFileMetadataFetcher metadataFetcher =
            new GenericFileMetadataFetcher();

    private IMetadataChecksummer metadataChecksummer = 
            new FileMetadataChecksummer();
    
    private IFileDocumentFetcher documentFetcher =
            new GenericFileDocumentFetcher();
    
    private IFileDocumentProcessor[] preImportProcessors;
    private IFileDocumentProcessor[] postImportProcessors;
    
    public FilesystemCrawlerConfig() {
        super();
    }

    public String[] getStartPaths() {
        return ArrayUtils.clone(startPaths);
    }
    public void setStartPaths(String[] startPaths) {
        this.startPaths = ArrayUtils.clone(startPaths);
    }
    public String[] getPathsFiles() {
        return ArrayUtils.clone(pathsFiles);
    }
    public void setPathsFiles(String[] pathsFiles) {
        this.pathsFiles = ArrayUtils.clone(pathsFiles);
    }
    /**
     * Gets the providers of paths used as starting points for crawling.
     * Use this approach over other methods when paths need to be provided
     * dynamicaly at launch time. Paths obtained by a provider are combined
     * with start paths provided through other methods.
     * @return a start paths provider
     * @since 2.7.0
     */
    public IStartPathsProvider[] getStartPathsProviders() {
        return startPathsProviders;
    }
    /**
     * Sets the providers of paths used as starting points for crawling.
     * Use this approach over other methods when paths need to be provided
     * dynamicaly at launch time. Paths obtained by a provider are combined
     * with start paths provided through other methods.
     * @param startPathsProviders start paths provider
     * @since 2.7.0
     */
    public void setStartPathsProviders(
            IStartPathsProvider... startPathsProviders) {
        this.startPathsProviders = startPathsProviders;
    }    
    public boolean isKeepDownloads() {
        return keepDownloads;
    }
    public void setKeepDownloads(boolean keepDownloads) {
        this.keepDownloads = keepDownloads;
    }

    public boolean isSkipDocumentFetch() {
        return skipDocumentFetch;
    }
    public void setSkipDocumentFetch(boolean skipDocumentFetch) {
        this.skipDocumentFetch = skipDocumentFetch;
    }

    /**
     * Gets the file system options provider. Default is
     * {@link GenericFilesystemOptionsProvider}.
     * @return file system options provider
     * @since 2.7.0
     */
    public IFilesystemOptionsProvider getOptionsProvider() {
        return optionsProvider;
    }
    /**
     * Sets the file system options provider. Cannot be <code>null</code>.
     * @param filesystemOptionsProvider file system options provider
     * @since 2.7.0
     */
    public void setOptionsProvider(
            IFilesystemOptionsProvider filesystemOptionsProvider) {
        this.optionsProvider = filesystemOptionsProvider;
    }

    /**
     * Gets the document metadata fetcher. Default is 
     * {@link GenericFileMetadataFetcher}.
     * @return metadata fetcher
     * @since 2.7.0
     */
    public IFileMetadataFetcher getMetadataFetcher() {
        return metadataFetcher;
    }
    /**
     * Sets the document metadata fetcher. Cannot be <code>null</code>.
     * @param metadataFetcher metadata fetcher
     * @since 2.7.0
     */
    public void setMetadataFetcher(IFileMetadataFetcher metadataFetcher) {
        this.metadataFetcher = metadataFetcher;
    }

    public IMetadataChecksummer getMetadataChecksummer() {
        return metadataChecksummer;
    }
    public void setMetadataChecksummer(
            IMetadataChecksummer metadataChecksummer) {
        this.metadataChecksummer = metadataChecksummer;
    }

    /**
     * Gets the document fetcher. Default is 
     * {@link GenericFileDocumentFetcher}.
     * @return document fetcher
     * @since 2.7.0
     */
    public IFileDocumentFetcher getDocumentFetcher() {
        return documentFetcher;
    }
    /**
     * Sets the document fetcher. Cannot be <code>null</code>.
     * @param documentFetcher document fetcher
     * @since 2.7.0
     */
    public void setDocumentFetcher(IFileDocumentFetcher documentFetcher) {
        this.documentFetcher = documentFetcher;
    }

    public IFileDocumentProcessor[] getPreImportProcessors() {
        return ArrayUtils.clone(preImportProcessors);
    }
    public void setPreImportProcessors(
            IFileDocumentProcessor[] filePostProcessors) {
        this.preImportProcessors = ArrayUtils.clone(filePostProcessors);
    }
    public IFileDocumentProcessor[] getPostImportProcessors() {
        return ArrayUtils.clone(postImportProcessors);
    }
    public void setPostImportProcessors(
            IFileDocumentProcessor[] filePostProcessors) {
        this.postImportProcessors = ArrayUtils.clone(filePostProcessors);
    }

    @Override
    protected void saveCrawlerConfigToXML(Writer out) throws IOException {
        try {
            EnhancedXMLStreamWriter writer = new EnhancedXMLStreamWriter(out);

            writer.writeElementBoolean("keepDownloads", isKeepDownloads());
            writer.writeElementBoolean("skipDocumentFetch", isSkipDocumentFetch());
            writer.writeStartElement("startPaths");
            
            String[] paths = getStartPaths();
            if (ArrayUtils.isNotEmpty(paths)) {
                for (String path : paths) {
                    writer.writeElementString("path", path);
                }
            }
            String[] files = getPathsFiles();
            if (ArrayUtils.isNotEmpty(files)) {
                for (String path : files) {
                    writer.writeElementString("pathsFile", path);
                }
            }
            writer.flush();
            IStartPathsProvider[] pathsProviders = getStartPathsProviders();
            if (ArrayUtils.isNotEmpty(pathsProviders)) {
                for (IStartPathsProvider provider : pathsProviders) {
                    writeObject(out, "provider", provider);
                }
            }
            out.flush();
            
            writer.writeEndElement();
            writer.flush();
            
            writeObject(out, "optionsProvider", getOptionsProvider());
            writeObject(out, "metadataFetcher", getMetadataFetcher());
            writeObject(out, "metadataChecksummer", getMetadataChecksummer());
            writeObject(out, "documentFetcher", getDocumentFetcher());
            writeArray(out, "preImportProcessors", 
                    "processor", getPreImportProcessors());
            writeArray(out, "postImportProcessors", 
                    "processor", getPostImportProcessors());
        } catch (XMLStreamException e) {
            throw new IOException(
                    "Could not write to XML config: " + getId(), e);
        }
    }

    @Override
    protected void loadCrawlerConfigFromXML(XMLConfiguration xml)
            throws IOException {
        //--- Simple Settings --------------------------------------------------
        loadSimpleSettings(xml);
        
        //--- FilesystemManager Factory ----------------------------------------
        setOptionsProvider(XMLConfigurationUtil.newInstance(xml,
                "optionsProvider", getOptionsProvider()));
        
        //--- Metadata Fetcher -------------------------------------------------
        setMetadataFetcher(XMLConfigurationUtil.newInstance(xml,
                "metadataFetcher", getMetadataFetcher()));
        
        //--- Metadata Checksummer ---------------------------------------------
        setMetadataChecksummer(XMLConfigurationUtil.newInstance(xml,
                "metadataChecksummer", getMetadataChecksummer()));

        //--- Document Fetcher -------------------------------------------------
        setDocumentFetcher(XMLConfigurationUtil.newInstance(xml,
                "documentFetcher", getDocumentFetcher()));

        //--- HTTP Pre-Processors ----------------------------------------------
        IFileDocumentProcessor[] preProcFilters = loadProcessors(xml,
                "preImportProcessors.processor");
        setPreImportProcessors(defaultIfEmpty(preProcFilters,
                getPreImportProcessors()));

        //--- HTTP Post-Processors ---------------------------------------------
        IFileDocumentProcessor[] postProcFilters = loadProcessors(xml,
                "postImportProcessors.processor");
        setPostImportProcessors(defaultIfEmpty(postProcFilters,
                getPostImportProcessors()));
    }
    
    private void loadSimpleSettings(XMLConfiguration xml) {
        setKeepDownloads(xml.getBoolean("keepDownloads", isKeepDownloads()));
        setSkipDocumentFetch(xml.getBoolean("skipDocumentFetch", isSkipDocumentFetch()));

        String[] startPathsArray = xml.getStringArray("startPaths.path");
        setStartPaths(defaultIfEmpty(startPathsArray, getStartPaths()));
        
        String[] pathsFilesArray = xml.getStringArray("startPaths.pathsFile");
        setPathsFiles(defaultIfEmpty(pathsFilesArray, getPathsFiles()));
        
        IStartPathsProvider[] startPathsProviders = 
                loadStartPathsProviders(xml);
        setStartPathsProviders(
                defaultIfEmpty(startPathsProviders, getStartPathsProviders()));        
    }
    
    private IStartPathsProvider[] loadStartPathsProviders(
            XMLConfiguration xml) {
        List<IStartPathsProvider> providers = new ArrayList<>();
        List<HierarchicalConfiguration> nodes = 
                xml.configurationsAt("startPaths.provider");
        for (HierarchicalConfiguration node : nodes) {
            IStartPathsProvider p = XMLConfigurationUtil.newInstance(node);
            providers.add(p);
            LOG.info("Start path provider loaded: " + p);
        }
        return providers.toArray(new IStartPathsProvider[] {});
    }
    
    private IFileDocumentProcessor[] loadProcessors(XMLConfiguration xml,
            String xmlPath) {
        List<IFileDocumentProcessor> filters = new ArrayList<>();
        List<HierarchicalConfiguration> filterNodes = xml
                .configurationsAt(xmlPath);
        for (HierarchicalConfiguration filterNode : filterNodes) {
            IFileDocumentProcessor filter = XMLConfigurationUtil
                    .newInstance(filterNode);
            filters.add(filter);
            LOG.info("HTTP document processor loaded: " + filter);
        }
        return filters.toArray(new IFileDocumentProcessor[] {});
    }
    
    @Override
    public boolean equals(final Object other) {
        if (!(other instanceof FilesystemCrawlerConfig)) {
            return false;
        }
        FilesystemCrawlerConfig castOther = (FilesystemCrawlerConfig) other;
        return new EqualsBuilder()
                .appendSuper(super.equals(castOther))
                .append(keepDownloads, castOther.keepDownloads)
                .append(skipDocumentFetch, castOther.skipDocumentFetch)
                .append(startPaths, castOther.startPaths)
                .append(pathsFiles, castOther.pathsFiles)
                .append(startPathsProviders, castOther.startPathsProviders)
                .append(optionsProvider, castOther.optionsProvider)
                .append(metadataFetcher, castOther.metadataFetcher)
                .append(metadataChecksummer, castOther.metadataChecksummer)
                .append(documentFetcher, castOther.documentFetcher)
                .append(preImportProcessors, castOther.preImportProcessors)
                .append(postImportProcessors, castOther.postImportProcessors)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .appendSuper(super.hashCode())
                .append(keepDownloads)
                .append(skipDocumentFetch)
                .append(startPaths)
                .append(pathsFiles)
                .append(startPathsProviders)
                .append(optionsProvider)
                .append(metadataFetcher)
                .append(metadataChecksummer)
                .append(documentFetcher)
                .append(preImportProcessors)
                .append(postImportProcessors)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .appendSuper(super.toString())
                .append("keepDownloads", keepDownloads)
                .append("skipDocumentFetch", skipDocumentFetch)
                .append("startPaths", startPaths)
                .append("pathsFiles", pathsFiles)
                .append("startPathsProviders", startPathsProviders)
                .append("optionsProvider", optionsProvider)
                .append("metadataFetcher", metadataFetcher)
                .append("metadataChecksummer", metadataChecksummer)
                .append("documentFetcher", documentFetcher)
                .append("preImportProcessors", preImportProcessors)
                .append("postImportProcessors", postImportProcessors)
                .toString();
    }  
}
