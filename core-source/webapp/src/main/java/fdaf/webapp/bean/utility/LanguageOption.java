/*
 * Copyright (c) Heru Himawan Tejo Laksono. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * 3. Neither the name of the copyright holders nor the names of its
 *    contributors may be used to endorse or promote products derived from this
 *    software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE
 * USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package fdaf.webapp.bean.utility;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.faces.model.SelectItem;
import javax.inject.Named;

@SessionScoped
@Named
public class LanguageOption implements Serializable {
    private static final long serialVersionUID = 1L;
    private SelectItem[] items = new SelectItem[]{};
    private HashMap<String, String> info = new HashMap<String, String>();
    private String[][] languages = {
        {"af-ZA", "Afrikaans (South Africa)"},
        {"am-ET", "Amharic (Ethiopia)"},
        {"ar-AE", "Arabic (United Arab Emirates)"},
        {"ar-BH", "Arabic (Bahrain)"},
        {"ar-DZ", "Arabic (Algeria)"},
        {"ar-EG", "Arabic (Egypt)"},
        {"ar-IL", "Arabic (Israel)"},
        {"ar-IQ", "Arabic (Iraq)"},
        {"ar-JO", "Arabic (Jordan)"},
        {"ar-KW", "Arabic (Kuwait)"},
        {"ar-LB", "Arabic (Lebanon)"},
        {"ar-MA", "Arabic (Morocco)"},
        {"ar-OM", "Arabic (Oman)"},
        {"ar-PS", "Arabic (State of Palestine)"},
        {"ar-QA", "Arabic (Qatar)"},
        {"ar-SA", "Arabic (Saudi Arabia)"},
        {"ar-TN", "Arabic (Tunisia)"},
        {"az-AZ", "Azerbaijani (Azerbaijan)"},
        {"bg-BG", "Bulgarian (Bulgaria)"},
        {"bn-BD", "Bengali (Bangladesh)"},
        {"bn-IN", "Bengali (India)"},
        {"ca-ES", "Catalan (Spain)"},
        {"cs-CZ", "Czech (Czech Republic)"},
        {"da-DK", "Danish (Denmark)"},
        {"de-DE", "German (Germany)"},
        {"el-GR", "Greek (Greece)"},
        {"en-AU", "English (Australia)"},
        {"en-CA", "English (Canada)"},
        {"en-GB", "English (United Kingdom)"},
        {"en-GH", "English (Ghana)"},
        {"en-IE", "English (Ireland)"},
        {"en-IN", "English (India)"},
        {"en-KE", "English (Kenya)"},
        {"en-NG", "English (Nigeria)"},
        {"en-NZ", "English (New Zealand)"},
        {"en-PH", "English (Philippines)"},
        {"en-SG", "English (Singapore)"},
        {"en-TZ", "English (Tanzania)"},
        {"en-US", "English (United States)"},
        {"en-ZA", "English (South Africa)"},
        {"es-AR", "Spanish (Argentina)"},
        {"es-BO", "Spanish (Bolivia)"},
        {"es-CL", "Spanish (Chile)"},
        {"es-CO", "Spanish (Colombia)"},
        {"es-CR", "Spanish (Costa Rica)"},
        {"es-DO", "Spanish (Dominican Republic)"},
        {"es-EC", "Spanish (Ecuador)"},
        {"es-ES", "Spanish (Spain)"},
        {"es-GT", "Spanish (Guatemala)"},
        {"es-HN", "Spanish (Honduras)"},
        {"es-MX", "Spanish (Mexico)"},
        {"es-NI", "Spanish (Nicaragua)"},
        {"es-PA", "Spanish (Panama)"},
        {"es-PE", "Spanish (Peru)"},
        {"es-PR", "Spanish (Puerto Rico)"},
        {"es-PY", "Spanish (Paraguay)"},
        {"es-SV", "Spanish (El Salvador)"},
        {"es-US", "Spanish (United States)"},
        {"es-UY", "Spanish (Uruguay)"},
        {"es-VE", "Spanish (Venezuela)"},
        {"eu-ES", "Basque (Spain)"},
        {"fa-IR", "Persian (Iran)"},
        {"fi-FI", "Finnish (Finland)"},
        {"fil-PH", "Filipino (Philippines)"},
        {"fr-CA", "French (Canada)"},
        {"fr-FR", "French (France)"},
        {"gl-ES", "Galician (Spain)"},
        {"gu-IN", "Gujarati (India)"},
        {"he-IL", "Hebrew (Israel)"},
        {"hi-IN", "Hindi (India)"},
        {"hr-HR", "Croatian (Croatia)"},
        {"hu-HU", "Hungarian (Hungary)"},
        {"hy-AM", "Armenian (Armenia)"},
        {"id-ID", "Indonesian (Indonesia)"},
        {"is-IS", "Icelandic (Iceland)"},
        {"it-IT", "Italian (Italy)"},
        {"ja-JP", "Japanese (Japan)"},
        {"jv-ID", "Javanese (Indonesia)"},
        {"ka-GE", "Georgian (Georgia)"},
        {"km-KH", "Khmer (Cambodia)"},
        {"kn-IN", "Kannada (India)"},
        {"ko-KR", "Korean (South Korea)"},
        {"lo-LA", "Lao (Laos)"},
        {"lt-LT", "Lithuanian (Lithuania)"},
        {"lv-LV", "Latvian (Latvia)"},
        {"ml-IN", "Malayalam (India)"},
        {"mr-IN", "Marathi (India)"},
        {"ms-MY", "Malay (Malaysia)"},
        {"nb-NO", "Norwegian Bokmål (Norway)"},
        {"ne-NP", "Nepali (Nepal)"},
        {"nl-NL", "Dutch (Netherlands)"},
        {"pl-PL", "Polish (Poland)"},
        {"pt-BR", "Portuguese (Brazil)"},
        {"pt-PT", "Portuguese (Portugal)"},
        {"ro-RO", "Romanian (Romania)"},
        {"ru-RU", "Russian (Russia)"},
        {"si-LK", "Sinhala (Sri Lanka)"},
        {"sk-SK", "Slovak (Slovakia)"},
        {"sl-SI", "Slovenian (Slovenia)"},
        {"sr-RS", "Serbian (Serbia)"},
        {"su-ID", "Sundanese (Indonesia)"},
        {"sv-SE", "Swedish (Sweden)"},
        {"sw-KE", "Swahili (Kenya)"},
        {"sw-TZ", "Swahili (Tanzania)"},
        {"ta-IN", "Tamil (India)"},
        {"ta-LK", "Tamil (Sri Lanka)"},
        {"ta-MY", "Tamil (Malaysia)"},
        {"ta-SG", "Tamil (Singapore)"},
        {"te-IN", "Telugu (India)"},
        {"th-TH", "Thai (Thailand)"},
        {"tr-TR", "Turkish (Turkey)"},
        {"uk-UA", "Ukrainian (Ukraine)"},
        {"ur-IN", "Urdu (India)"},
        {"ur-PK", "Urdu (Pakistan)"},
        {"vi-VN", "Vietnamese (Vietnam)"},
        {"yue-Hant-HK", "Chinese, Cantonese (Traditional, Hong Kong)"},
        {"zh", "Chinese, Mandarin (Simplified, China)"},
        {"zh-HK", "Chinese, Mandarin (Simplified, Hong Kong)"},
        {"zh-TW", "Chinese, Mandarin (Traditional, Taiwan)"},
        {"zu-ZA", "Zulu (South Africa)"}
    };
    
    public LanguageOption() {
        // NO-OP
    }

    @SuppressWarnings("unchecked")
    @PostConstruct
    public void postConstruct() {
        List<SelectItem> itemsTemp = new ArrayList<SelectItem>();
        for (String[] l : languages) {
            SelectItem selectItem = new SelectItem();
            selectItem.setValue(l[0]);
            selectItem.setLabel(l[0] + " :: " + l[1]);
            itemsTemp.add(selectItem);
            info.put(l[0], l[1]);
        }
        items = itemsTemp.toArray(new SelectItem[]{});
    }
    
    public SelectItem[] getItems() {
        return items;
    }
    
    public String getLanguageInfo(String code) {
        return info.get(code);
    }
}
