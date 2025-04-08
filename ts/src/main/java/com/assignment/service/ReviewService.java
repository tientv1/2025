package com.assignment.service;

import com.assignment.entity.Review;
import com.assignment.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    public List<Review> getReviewsByProductId(Integer productId) {
        return reviewRepository.findAll().stream()
                .filter(review -> review.getProduct().getId().equals(productId))
                .collect(Collectors.toList());
    }

    public Review saveReview(Review review) {
        return reviewRepository.save(review);
    }

    public void deleteReview(Integer id) {
        reviewRepository.deleteById(id);
    }
}
