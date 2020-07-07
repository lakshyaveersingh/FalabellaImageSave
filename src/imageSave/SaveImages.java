package imageSave;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.imageio.ImageIO;



public class SaveImages {

	public static final String UPDATE_IMAGE_STATUS_IN_PRODUCT_LINKS_TABLE = "UPDATE "+ CONSTANTS.DB_NAME+".product_links SET images_download_check=1 WHERE product_link_id = ?";
	
	
	
	@SuppressWarnings({"unchecked", "deprecated"})
	public static void main(String jk[]) throws SQLException
	{
		System.out.println("Image save process started...");
		Connection con = null;
		try{
			
			Class.forName(CONSTANTS.DRIVER_CLASS);
			con=DriverManager.getConnection(CONSTANTS.CONNECTION_URL, CONSTANTS.DB_USERNAME, CONSTANTS.DB_PASSWORD);
			Statement st;
			st = con.createStatement();
			String statement = "SELECT product_link_id, images, imageInformationSection, listing_link_id from "+ CONSTANTS.DB_NAME+".product_links WHERE "+ "images_download_check=0 AND " +" product_link_id BETWEEN "+Integer.parseInt(jk[0])+" AND "
			+Integer.parseInt(jk[1])+"";
			//System.out.println(statement);
			ResultSet rs = st.executeQuery(statement);
			while(rs.next()){
				int product_link_id = rs.getInt(1);
				String images = rs.getString(2);
				String imageInformationSection = rs.getString(3);
				int listing_link_id = rs.getInt(4);
				saveImages(images, product_link_id, "AI", listing_link_id);
				saveImages(imageInformationSection, product_link_id, "IIS", listing_link_id);
				
				try(Connection connection =DriverManager.getConnection(CONSTANTS.CONNECTION_URL, CONSTANTS.DB_USERNAME, CONSTANTS.DB_PASSWORD);
						PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_IMAGE_STATUS_IN_PRODUCT_LINKS_TABLE))
				{
					preparedStatement.setInt(1, product_link_id);
					preparedStatement.executeUpdate();
				}catch (SQLException e) {
					e.printStackTrace();;
				}
				
				
			}
		}catch (Exception e) {
			e.printStackTrace();
			if(!con.isClosed()){
				con.close();
			}
			System.out.println(e.getMessage());
		}
		System.out.println("Images saved!");
	}
	
	public static void saveImages(String images, int product_link_id, String imgInitName, int listing_link_id) throws IOException, SQLException, ClassNotFoundException{
		
		String[] s = images.split("\\|\\|");
		File f = new File("images");
		f.mkdir();
		int i = 0;
		
		for(String url : s)
		{
			url = url.trim();
			
			if(url.length()>0 && !url.equals("NA"))
			{
				
				i++;
				Connection con = null;
				try
				{
					URL imageURL = new URL(url);
					BufferedImage saveImage = ImageIO.read(imageURL);
					String image_name="";
					if(imgInitName.equals("AI"))
					{
						image_name="A_"+product_link_id+"_"+listing_link_id+"_"+i+".png";
						ImageIO.write(saveImage, "png", new File("images//"+"A_"+product_link_id+"_"+listing_link_id+"_"+i+".png"));
					}
					else if(imgInitName.equals("IIS"))
					{
						image_name = product_link_id+"_"+listing_link_id+"_"+i+".png";
						ImageIO.write(saveImage, "png", new File("images//"+product_link_id+"_"+listing_link_id+"_"+i+".png"));
					}
				   
				    Class.forName(CONSTANTS.DRIVER_CLASS);
					con=DriverManager.getConnection(CONSTANTS.CONNECTION_URL, CONSTANTS.DB_USERNAME, CONSTANTS.DB_PASSWORD);
					//Statement st = con.createStatement();
					String sql = "INSERT INTO "+CONSTANTS.DB_NAME+".images_done(product_link_id, urls_done, check_flag, image_name) VALUES(?, ?, ?, ?)";
					PreparedStatement statement = con.prepareStatement(sql);
					statement.setInt(1, product_link_id);
					statement.setString(2, url);
					statement.setString(3, imgInitName);
					statement.setString(4, image_name);
					
					int a =statement.executeUpdate();
					
					statement.close();
					con.close();
				}
				catch (Exception e)
				{
					try{
					Class.forName(CONSTANTS.DRIVER_CLASS);
					con=DriverManager.getConnection(CONSTANTS.CONNECTION_URL, CONSTANTS.DB_USERNAME, CONSTANTS.DB_PASSWORD);
					//Statement st = con.createStatement();
					String sql = "INSERT INTO "+CONSTANTS.DB_NAME+".images_not_done(product_link_id, urls_not_done, check_flag) VALUES(?, ?, ?)";
					PreparedStatement statement = con.prepareStatement(sql);
					statement.setInt(1, product_link_id);
					statement.setString(2, url);
					statement.setString(3, imgInitName);
					statement.executeUpdate(sql);
					statement.close();
					con.close();
					}catch (Exception ex) {
						System.out.println(ex);
					}
				}
			}
		}
		
		
	}

}
