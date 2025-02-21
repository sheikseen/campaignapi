package com.shiel.campaignapi.seeder;

import java.util.Map;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.shiel.campaignapi.entity.Country;
import com.shiel.campaignapi.entity.Role;
import com.shiel.campaignapi.repository.CountryRepository;
import com.shiel.campaignapi.repository.RoleRepository;

import java.util.LinkedHashMap;


@Component
public class DataSeeder implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final CountryRepository countryRepository;

    public DataSeeder(RoleRepository roleRepository, CountryRepository countryRepository) {
        this.roleRepository = roleRepository;
        this.countryRepository = countryRepository;
    }

    @Override
    public void run(String... args) throws Exception {
    	if (roleRepository.count() > 0) {
	        return;
	    }
    	Map<String, String> roles = Map.of(
    	        "ROLE_ADMIN", "Administrator with full access",
    	        "ROLE_USER", "Regular user with limited access",
    	        "ROLE_MANAGER", "Manager with advanced permissions",
    	        "ROLE_APPLICATION", "System application role"
    	    );

    	for (Map.Entry<String, String> entry : roles.entrySet()) {
            String roleName = entry.getKey();
            String description = entry.getValue();

            if (roleRepository.findByRoleName(roleName).isEmpty()) {
                Role role = new Role();
                role.setRoleName(roleName);
                role.setDescription(description); 
                roleRepository.save(role);
            }
        }
    	
    	
    	  if (countryRepository.count() > 0) {
    	        return;
    	    }
    	   Map<String, String> countryCodes = new LinkedHashMap<>();


    		       countryCodes.put("Albania", "+355");
    		        countryCodes.put("Algeria", "+213");
    		        countryCodes.put("Argentina", "+54");
    		        countryCodes.put("Armenia", "+374");
    		        countryCodes.put("Australia", "+61");
    		        countryCodes.put("Austria", "+43");
    		        countryCodes.put("Azerbaijan", "+994");
    		        countryCodes.put("Bahrain", "+973");
    		        countryCodes.put("Belgium", "+32");
    		        countryCodes.put("Bhutan", "+975");
    		        countryCodes.put("Brazil", "+55");
    		        countryCodes.put("Canada", "+1");
    		        countryCodes.put("Chile", "+56");
    		        countryCodes.put("China", "+86");
    		        countryCodes.put("Cyprus", "+357");
    		        countryCodes.put("Denmark", "+45");
    		        countryCodes.put("Egypt", "+20");
    		        countryCodes.put("Ethiopia", "+251");
    		        countryCodes.put("Finland", "+358");
    		        countryCodes.put("Fiji", "+679");
    		        countryCodes.put("France", "+33");
    		        countryCodes.put("Georgia", "+995");
    		        countryCodes.put("Germany", "+49");
    		        countryCodes.put("Greece", "+30");
    		        countryCodes.put("Hungary", "+36");
    		        countryCodes.put("Iceland", "+354");
    		        countryCodes.put("India", "+91");
    		        countryCodes.put("Iran", "+98");
    		        countryCodes.put("Iraq", "+964");
    		        countryCodes.put("Ireland", "+353");
    		        countryCodes.put("Israel", "+972");
    		        countryCodes.put("Italy", "+39");
    		        countryCodes.put("Jordan", "+962");
    		        countryCodes.put("Korea (North)", "+850");
    		        countryCodes.put("Korea (South)", "+82");
    		        countryCodes.put("Malaysia", "+60");
    		        countryCodes.put("Maldives", "+960");
    		        countryCodes.put("Malta", "+356");
    		        countryCodes.put("Mexico", "+52");
    		        countryCodes.put("Nepal", "+977");
    		        countryCodes.put("Netherlands", "+31");
    		        countryCodes.put("New Zealand", "+64");
    		        countryCodes.put("Nigeria", "+234");
    		        countryCodes.put("North Macedonia", "+389");
    		        countryCodes.put("Norway", "+47");
    		        countryCodes.put("Oman", "+968");
    		        countryCodes.put("Philippines", "+63");
    		        countryCodes.put("Poland", "+48");
    		        countryCodes.put("Portugal", "+351");
    		        countryCodes.put("Qatar", "+974");
    		        countryCodes.put("Romania", "+40");
    		        countryCodes.put("Russia", "+7");
    		        countryCodes.put("Serbia", "+381");
    		        countryCodes.put("Singapore", "+65");
    		        countryCodes.put("Somalia", "+252");
    		        countryCodes.put("South Africa", "+27");
    		        countryCodes.put("South Sudan", "+211");
    		        countryCodes.put("Spain", "+34");
    		        countryCodes.put("Sri Lanka", "+94");
    		        countryCodes.put("Sudan", "+249");
    		        countryCodes.put("Turkey", "+90");
    		        countryCodes.put("Uganda", "+256");
    		        countryCodes.put("Ukraine", "+380");
    		        countryCodes.put("United Arab Emirates", "+971");
    		        countryCodes.put("United States", "+1");
    		        countryCodes.put("United Kingdom", "+44");
    		        countryCodes.put("Uzbekistan", "+998");
    		        countryCodes.put("Vanuatu", "+678");
    		        countryCodes.put("Vatican City", "+39");
    	 countryCodes.forEach((country, countryCode) -> System.out.println(country + ": " + countryCode));

    	
    	for (Map.Entry<String, String> entry : countryCodes.entrySet()) {
            String countryCode = entry.getValue();
            String country = entry.getKey();

            if (countryRepository.findByCountryCode(country).isEmpty()) {
                Country countries1= new Country();
                countries1.setCountryCode(countryCode); 
                countries1.setCountry(country);
                countryRepository.save(countries1);
            }
        }
    	
    	
    
        System.out.println("Roles and Countries seeded successfully!");
    }
}