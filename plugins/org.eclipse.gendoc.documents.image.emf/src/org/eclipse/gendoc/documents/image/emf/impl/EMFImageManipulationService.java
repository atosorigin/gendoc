/*****************************************************************************
 * Copyright (c) 2016 Atos
 *
 *    
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Jean-François Rolland (ATOS) - Initial API and implementation
 * Tristan Faure (ATOS)
 *
 *****************************************************************************/
package org.eclipse.gendoc.documents.image.emf.impl;

import java.awt.Color;
import java.awt.Dimension;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import javax.imageio.ImageIO;

import org.eclipse.core.runtime.IPath;
import org.eclipse.gendoc.documents.IImageManipulationService;
import org.eclipse.gendoc.documents.ImageDimension;
import org.eclipse.gendoc.services.exception.AdditionalResourceException;
import org.freehep.graphicsio.emf.EMFInputStream;
import org.freehep.graphicsio.emf.EMFRenderer;

import de.vandermeer.svg2vector.base.TargetProperties;
import de.vandermeer.svg2vector.converters.Svg2Emf;

public class EMFImageManipulationService implements IImageManipulationService {

	public ImageDimension getImageDimension(String imagePath) throws IOException, AdditionalResourceException {
		
		if (imagePath == null || !imagePath.toLowerCase().endsWith("emf")){
			throw new AdditionalResourceException("Cannot get dimension of image '"+imagePath+"'.");
		}
		
		try{
			InputStream is = new FileInputStream(imagePath);
			EMFInputStream emfIS = new EMFInputStream(is);
			EMFRenderer emfReindeer	= new EMFRenderer(emfIS) ;
			Dimension dim = emfReindeer.getSize() ;
		
			ImageDimension result = new ImageDimension();
			result.setWidth(dim.getWidth());
			result.setHeight(dim.getHeight());
			is.close();

			return result;
		} catch (FileNotFoundException fnfe){
			throw new AdditionalResourceException("Cannot open file '"+imagePath+"'.", fnfe);
		} catch(IOException ioe){
			throw new AdditionalResourceException("Cannot load image '"+imagePath+"'.", ioe);
		}
	}
	
	public String renameExtension(String extension) {
		return "SVG";
	}

	public void transform(IPath path) {
		Svg2Emf svg2emf = new Svg2Emf();
		URI svg_URI_input;
		svg_URI_input = path.toFile().toURI();
		
		svg2emf.load(svg_URI_input);
		TargetProperties properties = new TargetProperties() {
			
			@Override
			public void setPropertyTransparent(boolean on) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void setPropertyBackgroundColor(Color color) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void setPropertyBackground(boolean on) {
				// TODO Auto-generated method stub
				
			}
		};
		svg2emf.setProperties(properties);
		
		
		String fileName = path.lastSegment();
		fileName = fileName.replace(".svg", "");
		
		String directory = path.removeLastSegments(1).toPortableString() ;
//		Service.(RawImageWriterSpi.class);
//		ServiceLoader.load(EMFExportFileType.class);
//		IIORegistry.getDefaultInstance().registerServiceProvider(
//				RawImageWriterSpi.class);
//		IIORegistry.getDefaultInstance().registerServiceProvider(
//				EMFExportFileType.class);
		ImageIO.scanForPlugins();
		svg2emf.convert(directory, fileName);
		File f = path.toFile();
		if (f != null && f.exists()){
			f.delete();
		}
	}

}
