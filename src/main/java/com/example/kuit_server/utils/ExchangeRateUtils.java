package com.example.kuit_server.utils;

import com.example.kuit_server.common.exception.BadRequestException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static com.example.kuit_server.common.response.status.BaseExceptionResponseStatus.EXCHANGE_RATE_NOT_FOUND;

public class ExchangeRateUtils {
    public static double getExchangeRate () {

        BufferedReader reader;
        String line;
        StringBuffer responseContent = new StringBuffer();
        JSONParser parser = new JSONParser();

        String authKey = "H5QBYLwATmUSxRzr02EhowyEiXiXSMXG";
        String searchDate = new SimpleDateFormat("yyyyMMdd").format(new Date());
        String dataType = "AP01";
        double exchangeRate = 0.0;

        HttpURLConnection connection = null;
        try {
            // Request URL
            URL url = new URL("https://www.koreaexim.go.kr/site/program/financial/exchangeJSON?authkey=" + authKey + "&searchdate=" + searchDate + "&data=" + dataType);
            connection = (HttpURLConnection) url.openConnection();

            // Request 초기 세팅
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            int status = connection.getResponseCode();

            // API 호출
            // 실패했을 경우 Connection Close
            if (status > 299) {
                reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
                while ((line = reader.readLine()) != null) {
                    responseContent.append(line);
                }
                reader.close();
            } else { // 성공했을 경우 환율 정보 추출
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                while ((line = reader.readLine()) != null) {
                    JSONArray exchangeRateInfoList = (JSONArray) parser.parse(line);

                    // KRW -> USD에 대한 환율 정보 조회
                    for (Object o : exchangeRateInfoList) {
                        JSONObject exchangeRateInfo = (JSONObject) o;
                        if (exchangeRateInfo.get("cur_unit").equals("USD")) {

                            // 쉼표가 포함되어 String 형태로 들어오는 데이터를 Double로 파싱하기 위한 부분
                            NumberFormat format = NumberFormat.getInstance(Locale.getDefault());
                            exchangeRate = format.parse(exchangeRateInfo.get("deal_bas_r").toString()).doubleValue();
                        }
                    }
                }
                reader.close();
            }
            System.out.println(responseContent.toString());

        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        } catch (java.text.ParseException e) {
            throw new RuntimeException(e);
        } finally {
            connection.disconnect();
        }

        if (exchangeRate == 0) {
            throw new BadRequestException(EXCHANGE_RATE_NOT_FOUND);
        }

        return exchangeRate;
    }
}
