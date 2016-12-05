package api;

public class RegisterQueryEntity {
	private String tid, cid, temp_lower, temp_upper, humid_lower, humid_upper, hit_upper, door, notice, sensing, location_code, operator, phone, message, switchNo, switchButton;
	private String info_tid, info_cid, info_temp, info_humid, info_hit, info_location, info_latitude, info_longitude, info_door;

	public String getTid() {
		return tid;
	}

	public void setTid(String tid) {
		this.tid = tid;
	}

	public String getCid() {
		return cid;
	}

	public void setCid(String cid) {
		this.cid = cid;
	}

	public String getTemp_lower() {
		return temp_lower;
	}

	public void setTemp_lower(String temp_lower) {
		this.temp_lower = temp_lower;
	}

	public String getTemp_upper() {
		return temp_upper;
	}

	public void setTemp_upper(String temp_upper) {
		this.temp_upper = temp_upper;
	}

	public String getHumid_lower() {
		return humid_lower;
	}

	public void setHumid_lower(String humid_lower) {
		this.humid_lower = humid_lower;
	}

	public String getHumid_upper() {
		return humid_upper;
	}

	public void setHumid_upper(String humid_upper) {
		this.humid_upper = humid_upper;
	}

	public String getHit_upper() {
		return hit_upper;
	}

	public void setHit_upper(String hit_upper) {
		this.hit_upper = hit_upper;
	}

	public String getDoor() {
		return door;
	}

	public void setDoor(String door) {
		this.door = door;
	}

	public String getNotice() {
		return notice;
	}

	public void setNotice(String notice) {
		this.notice = notice;
	}

	public String getSensing() {
		return sensing;
	}

	public void setSensing(String sensing) {
		this.sensing = sensing;
	}

	public String getLocation_code() {
		return location_code;
	}

	public void setLocation_code(String location_code) {
		this.location_code = location_code;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getSwitchNo() {
		return switchNo;
	}

	public void setSwitchNo(String switchNo) {
		this.switchNo = switchNo;
	}

	public String getSwitchButton() {
		return switchButton;
	}

	public void setSwitchButton(String switchButton) {
		this.switchButton = switchButton;
	}

	public String getInfo_tid() {
		return info_tid;
	}

	public void setInfo_tid(String info_tid) {
		this.info_tid = info_tid;
	}

	public String getInfo_cid() {
		return info_cid;
	}

	public void setInfo_cid(String info_cid) {
		this.info_cid = info_cid;
	}

	public String getInfo_temp() {
		return info_temp;
	}

	public void setInfo_temp(String info_temp) {
		this.info_temp = info_temp;
	}

	public String getInfo_humid() {
		return info_humid;
	}

	public void setInfo_humid(String info_humid) {
		this.info_humid = info_humid;
	}

	public String getInfo_hit() {
		return info_hit;
	}

	public void setInfo_hit(String info_hit) {
		this.info_hit = info_hit;
	}

	public String getInfo_location() {
		return info_location;
	}

	public void setInfo_location(String info_location) {
		this.info_location = info_location;
	}

	public String getInfo_latitude() {
		return info_latitude;
	}

	public void setInfo_latitude(String info_latitude) {
		this.info_latitude = info_latitude;
	}

	public String getInfo_longitude() {
		return info_longitude;
	}

	public void setInfo_longitude(String info_longitude) {
		this.info_longitude = info_longitude;
	}

	public String getInfo_door() {
		return info_door;
	}

	public void setInfo_door(String info_door) {
		this.info_door = info_door;
	}

	@Override
	public String toString() {
		return "RegisterQueryEntity [tid=" + tid + ", cid=" + cid
				+ ", temp_lower=" + temp_lower + ", temp_upper=" + temp_upper
				+ ", humid_lower=" + humid_lower + ", humid_upper="
				+ humid_upper + ", hit_upper=" + hit_upper + ", door=" + door
				+ ", notice=" + notice + ", sensing=" + sensing
				+ ", location_code=" + location_code + ", operator=" + operator
				+ ", phone=" + phone + ", message=" + message + ", switchNo="
				+ switchNo + ", switchButton=" + switchButton + ", info_tid="
				+ info_tid + ", info_cid=" + info_cid + ", info_temp="
				+ info_temp + ", info_humid=" + info_humid + ", info_hit="
				+ info_hit + ", info_location=" + info_location
				+ ", info_latitude=" + info_latitude + ", info_longitude="
				+ info_longitude + ", info_door=" + info_door + "]";
	}
}
