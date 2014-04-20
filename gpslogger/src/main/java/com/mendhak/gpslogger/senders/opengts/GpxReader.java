/*
*    This file is part of GPSLogger for Android.
*
*    GPSLogger for Android is free software: you can redistribute it and/or modify
*    it under the terms of the GNU General Public License as published by
*    the Free Software Foundation, either version 2 of the License, or
*    (at your option) any later version.
*
*    GPSLogger for Android is distributed in the hope that it will be useful,
*    but WITHOUT ANY WARRANTY; without even the implied warranty of
*    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*    GNU General Public License for more details.
*
*    You should have received a copy of the GNU General Public License
*    along with GPSLogger for Android.  If not, see <http://www.gnu.org/licenses/>.
*/

package com.mendhak.gpslogger.senders.opengts;

import android.location.Location;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

/**
 * GpxReader
 * <p/>
 * http://stackoverflow.com/questions/9417189/parsing-gpx-files-with-sax-parer-or-xmlpullparser
 *
 * @author Droid_Interceptor @ http://stackoverflow.com
 */
public class GpxReader {

    private static final SimpleDateFormat gpxDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

    public static List<Location> getPoints(File gpxFile) throws Exception {
        List<Location> points;

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();

        FileInputStream fis = new FileInputStream(gpxFile);
        Document dom = builder.parse(fis);
        Element root = dom.getDocumentElement();
        NodeList items = root.getElementsByTagName("trkpt");

        points = new ArrayList<Location>();

        for (int j = 0; j < items.getLength(); j++) {
            Node item = items.item(j);
            NamedNodeMap attrs = item.getAttributes();
            NodeList props = item.getChildNodes();

            Location pt = new Location("test");

            pt.setLatitude(Double.parseDouble(attrs.getNamedItem("lat").getNodeValue()));
            pt.setLongitude(Double.parseDouble(attrs.getNamedItem("lon").getNodeValue()));

            for (int k = 0; k < props.getLength(); k++) {
                Node item2 = props.item(k);
                String name = item2.getNodeName();

                if (name.equalsIgnoreCase("ele")) {
                    pt.setAltitude(Double.parseDouble(item2.getFirstChild().getNodeValue()));
                }
                if (name.equalsIgnoreCase("course")) {
                    pt.setBearing(Float.parseFloat(item2.getFirstChild().getNodeValue()));
                }
                if (name.equalsIgnoreCase("speed")) {
                    pt.setSpeed(Float.parseFloat(item2.getFirstChild().getNodeValue()));
                }
                if (name.equalsIgnoreCase("hdop")) {
                    pt.setAccuracy(Float.parseFloat(item2.getFirstChild().getNodeValue()) * 5);
                }
                if (name.equalsIgnoreCase("time")) {
                    pt.setTime((getDateFormatter().parse(item2.getFirstChild().getNodeValue())).getTime());
                }

            }

            for (int y = 0; y < props.getLength(); y++) {
                Node item3 = props.item(y);
                String name = item3.getNodeName();
                if (!name.equalsIgnoreCase("ele")) {
                    continue;
                }
                pt.setAltitude(Double.parseDouble(item3.getFirstChild().getNodeValue()));
            }

            points.add(pt);

        }

        fis.close();

        return points;
    }

    public static SimpleDateFormat getDateFormatter() {
        SimpleDateFormat sdf = (SimpleDateFormat) gpxDate.clone();
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        return sdf;
    }

}
