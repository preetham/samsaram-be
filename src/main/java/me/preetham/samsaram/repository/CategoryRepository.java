package me.preetham.samsaram.repository;

import me.preetham.samsaram.model.Category;
import org.springframework.data.repository.CrudRepository;

public interface CategoryRepository extends CrudRepository<Category, Integer> {
}
