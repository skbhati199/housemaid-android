package com.housemaid.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by fluper on 24/5/18.
 */

public class UserPetProblemModel implements Serializable {

    @SerializedName("user_id")
    private int user_id;

    @SerializedName("pet_problem_id")
    private int pet_problem_id;

    @SerializedName("pet_problem_detail")
    private PetProblemDetailModel petProblemDetailModel;

    @SerializedName("id")
    private int id;

    @SerializedName("job_post_id")
    private int job_post_id;

    @SerializedName("pet_problem_name")
    private String pet_problem_name;

    public int getId() {
        return id;
    }

    public int getJob_post_id() {
        return job_post_id;
    }

    public String getPet_problem_name() {
        return pet_problem_name;
    }

    public int getUser_id() {
        return user_id;
    }

    public int getPet_problem_id() {
        return pet_problem_id;
    }

    public PetProblemDetailModel getPetProblemDetailModel() {
        return petProblemDetailModel;
    }

    public class PetProblemDetailModel implements Serializable {
        @SerializedName("id")
        private int id;

        @SerializedName("name")
        private String name;

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }
    }
}
